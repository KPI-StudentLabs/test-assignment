/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import ua.kpi.comsys.test2.NumberList;

/**
 * Реалізація кільцевого однонаправленого списку для зберігання чисел у двійковій системі.
 * Кожен елемент списку зберігає одну двійкову цифру (0 або 1).
 *
 * @author Коваль Богдан Андрійович
 * Група: ІС-31
 * Номер залікової книжки: 10
 *
 * Параметри завдання (на основі номера залікової 10):
 * - С3 = 10 % 3 = 1: Кільцевий однонаправлений список
 * - С5 = 10 % 5 = 0: Двійкова система числення (основа 2)
 * - С7 = 10 % 7 = 3: Ціла частина від ділення
 * - Додаткова система: (0+1) % 5 = 1: Трійкова система (основа 3)
 */
public class NumberListImpl implements NumberList {

    private static final int BASE = 2; // двійкова система
    private static final int ADDITIONAL_BASE = 3; // трійкова система

    private Node head; // голова списку
    private int size; // розмір списку
    private int currentBase; // поточна система числення цього списку

    /**
     * Клас для вузла списку
     */
    private class Node {
        Byte data; // дані вузла
        Node next; // посилання на наступний елемент

        Node(Byte data) {
            this.data = data;
            this.next = null;
        }
    }

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.size = 0;
        this.currentBase = BASE; // за замовчуванням використовуємо основну систему (двійкову)
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                // конвертуємо десяткове число у двійкове
                initializeFromDecimalString(line.trim());
            }
        } catch (Exception e) {
            // якщо помилка - залишаємо список порожнім
            // (наприклад, файл не знайдено або неправильний формат)
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        if (value != null && !value.trim().isEmpty()) {
            // ініціалізуємо список з десяткового числа
            initializeFromDecimalString(value.trim());
        }
    }

    /**
     * Допоміжний метод для ініціалізації списку з десяткового рядка
     */
    private void initializeFromDecimalString(String decimal) {
        try {
            BigInteger num = new BigInteger(decimal);
            if (num.signum() <= 0) {
                return; // якщо число 0 або від'ємне, список залишається порожнім
            }

            // переводимо число в двійкову систему
            String binary = num.toString(BASE);
            // додаємо кожну цифру до списку
            for (int i = 0; i < binary.length(); i++) {
                add((byte) (binary.charAt(i) - '0'));
            }
        } catch (NumberFormatException e) {
            // якщо рядок не є валідним числом - залишаємо список порожнім
        }
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toDecimalString());
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 10;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        if (isEmpty()) {
            return new NumberListImpl();
        }

        // спочатку конвертуємо в десяткову систему
        String decimal = toDecimalString();
        BigInteger num = new BigInteger(decimal);
        // потім переводимо в трійкову систему
        String ternary = num.toString(ADDITIONAL_BASE);

        // створюємо новий список для трійкової системи
        NumberListImpl result = new NumberListImpl();
        result.currentBase = ADDITIONAL_BASE; // встановлюємо трійкову систему для нового списку
        for (int i = 0; i < ternary.length(); i++) {
            result.add((byte) (ternary.charAt(i) - '0'));
        }

        return result;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Performs integer division (this / arg).
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation (divisor)
     *
     * @return result of additional operation (quotient).
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        // перевіряємо чи не ділимо на нуль
        if (arg == null || arg.isEmpty()) {
            throw new ArithmeticException("Division by zero");
        }

        NumberListImpl dividend = this; // ділене
        NumberListImpl divisor = (NumberListImpl) arg; // дільник

        // переводимо обидва числа в десяткову систему
        String dividendDecimal = dividend.toDecimalString();
        String divisorDecimal = divisor.toDecimalString();

        BigInteger dividendNum = new BigInteger(dividendDecimal);
        BigInteger divisorNum = new BigInteger(divisorDecimal);

        if (divisorNum.signum() == 0) {
            throw new ArithmeticException("Division by zero");
        }

        // виконуємо ділення та повертаємо результат
        BigInteger quotient = dividendNum.divide(divisorNum);

        return new NumberListImpl(quotient.toString());
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) {
            return "0";
        }

        // збираємо всі цифри в рядок
        StringBuilder digits = new StringBuilder();
        Node current = head;
        do {
            digits.append(current.data);
            current = current.next;
        } while (current != head);

        // конвертуємо з поточної системи числення в десяткову
        BigInteger num = new BigInteger(digits.toString(), currentBase);
        return num.toString();
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }

        // повертаємо число як рядок цифр (без розділювачів)
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            sb.append(current.data);
            current = current.next;
        } while (current != head);

        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberList)) return false;

        NumberList other = (NumberList) o;
        if (this.size() != other.size()) return false;

        // порівнюємо елемент за елементом
        Iterator<Byte> it1 = this.iterator();
        Iterator<Byte> it2 = other.iterator();

        while (it1.hasNext()) {
            if (!it1.next().equals(it2.next())) {
                return false;
            }
        }

        return true;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (isEmpty() || !(o instanceof Byte)) {
            return false;
        }

        Node current = head;
        do {
            if (current.data.equals(o)) {
                return true;
            }
            current = current.next;
        } while (current != head);

        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int count = 0; // лічильник пройдених елементів

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Byte data = current.data;
                current = current.next; // переходимо до наступного
                count++;
                return data;
            }
        };
    }


    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int index = 0;

        if (!isEmpty()) {
            Node current = head;
            do {
                array[index++] = current.data;
                current = current.next;
            } while (current != head);
        }

        return array;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        // Not implemented as per assignment requirements
        return null;
    }


    @Override
    public boolean add(Byte e) {
        if (e == null) {
            throw new NullPointerException("Null elements are not permitted");
        }

        Node newNode = new Node(e);

        if (isEmpty()) {
            // якщо список порожній, створюємо перший елемент
            head = newNode;
            newNode.next = head; // вказуємо на себе (кільце)
        } else {
            // знаходимо останній елемент
            Node tail = getTail();
            tail.next = newNode;
            newNode.next = head; // замикаємо кільце
        }

        size++;
        return true;
    }


    @Override
    public boolean remove(Object o) {
        if (isEmpty() || !(o instanceof Byte)) {
            return false;
        }

        // якщо видаляємо перший елемент
        if (head.data.equals(o)) {
            if (size == 1) {
                head = null;
            } else {
                Node tail = getTail();
                head = head.next;
                tail.next = head; // оновлюємо кільце
            }
            size--;
            return true;
        }

        // шукаємо елемент для видалення
        Node current = head;
        do {
            if (current.next.data.equals(o)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        } while (current != head);

        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte item : c) {
            if (add(item)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        boolean modified = false;
        for (Byte item : c) {
            add(index++, item);
            modified = true;
        }
        return modified;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object item : c) {
            while (remove(item)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<Byte> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public void clear() {
        head = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current.data;
    }


    @Override
    public Byte set(int index, Byte element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (element == null) {
            throw new NullPointerException("Null elements are not permitted");
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        Byte oldValue = current.data;
        current.data = element;
        return oldValue;
    }


    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (element == null) {
            throw new NullPointerException("Null elements are not permitted");
        }

        // якщо додаємо на початок списку
        if (index == 0) {
            Node newNode = new Node(element);
            if (isEmpty()) {
                head = newNode;
                newNode.next = head; // вказуємо сам на себе
            } else {
                Node tail = getTail();
                newNode.next = head;
                head = newNode;
                tail.next = head; // замикаємо кільце
            }
            size++;
            return;
        }

        // знаходимо елемент перед позицією вставки
        Node current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }

        // вставляємо новий елемент
        Node newNode = new Node(element);
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }


    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Byte removedValue;

        // якщо видаляємо перший елемент
        if (index == 0) {
            removedValue = head.data;
            if (size == 1) {
                head = null; // список стає порожнім
            } else {
                Node tail = getTail();
                head = head.next;
                tail.next = head; // підтримуємо кільцеву структуру
            }
            size--;
            return removedValue;
        }

        // знаходимо елемент перед тим, який треба видалити
        Node current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }

        // зберігаємо значення та видаляємо елемент
        removedValue = current.next.data;
        current.next = current.next.next;
        size--;

        return removedValue;
    }


    @Override
    public int indexOf(Object o) {
        if (isEmpty() || !(o instanceof Byte)) {
            return -1;
        }

        Node current = head;
        int index = 0;
        do {
            if (current.data.equals(o)) {
                return index;
            }
            current = current.next;
            index++;
        } while (current != head);

        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (isEmpty() || !(o instanceof Byte)) {
            return -1;
        }

        Node current = head;
        int index = 0;
        int lastIndex = -1;

        do {
            if (current.data.equals(o)) {
                lastIndex = index;
            }
            current = current.next;
            index++;
        } while (current != head);

        return lastIndex;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        return new NumberListIterator(0);
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return new NumberListIterator(index);
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", Size: " + size);
        }

        // створюємо новий список з елементів у заданому діапазоні
        NumberListImpl subList = new NumberListImpl();
        subList.currentBase = this.currentBase; // зберігаємо ту саму систему числення
        for (int i = fromIndex; i < toIndex; i++) {
            subList.add(get(i));
        }

        return subList;
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }

        if (index1 == index2) {
            return true; // якщо індекси однакові, нічого не робимо
        }

        // міняємо елементи місцями
        Byte temp = get(index1);
        set(index1, get(index2));
        set(index2, temp);

        return true;
    }


    @Override
    public void sortAscending() {
        if (size <= 1) {
            return;
        }

        // сортування бульбашкою (по зростанню)
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) > get(j + 1)) {
                    swap(j, j + 1);
                }
            }
        }
    }


    @Override
    public void sortDescending() {
        if (size <= 1) {
            return;
        }

        // сортування бульбашкою (по спаданню)
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (get(j) < get(j + 1)) {
                    swap(j, j + 1);
                }
            }
        }
    }


    @Override
    public void shiftLeft() {
        if (size <= 1) {
            return;
        }

        // циклічний зсув вліво - просто переміщуємо голову на один вузол вперед
        head = head.next;
    }


    @Override
    public void shiftRight() {
        if (size <= 1) {
            return;
        }

        // циклічний зсув вправо - переміщуємо голову на останній елемент
        head = getTail();
    }

    /**
     * Допоміжний метод для отримання останнього вузла списку
     */
    private Node getTail() {
        if (isEmpty()) {
            return null;
        }

        // йдемо по списку поки не дійдемо до останнього елемента
        Node current = head;
        while (current.next != head) {
            current = current.next;
        }
        return current;
    }

    /**
     * Реалізація ListIterator для обходу списку
     */
    private class NumberListIterator implements ListIterator<Byte> {
        private Node current;
        private Node lastReturned;
        private int currentIndex;
        private int expectedSize;

        NumberListIterator(int index) {
            this.currentIndex = index;
            this.expectedSize = size;
            this.lastReturned = null;

            if (isEmpty()) {
                current = null;
            } else if (index == size) {
                current = null;
            } else {
                // переходимо до потрібної позиції
                current = head;
                for (int i = 0; i < index; i++) {
                    current = current.next;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public Byte next() {
            checkModification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            lastReturned = current;
            Byte data = current.data;
            current = current.next;
            currentIndex++;

            return data;
        }

        @Override
        public boolean hasPrevious() {
            return currentIndex > 0;
        }

        @Override
        public Byte previous() {
            checkModification();
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }

            currentIndex--;
            // знаходимо попередній елемент
            if (currentIndex == 0) {
                current = head;
            } else {
                current = head;
                for (int i = 0; i < currentIndex; i++) {
                    current = current.next;
                }
            }
            lastReturned = current;

            return current.data;
        }

        @Override
        public int nextIndex() {
            return currentIndex;
        }

        @Override
        public int previousIndex() {
            return currentIndex - 1;
        }

        @Override
        public void remove() {
            checkModification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            NumberListImpl.this.remove(currentIndex - 1);
            currentIndex--;
            expectedSize--;
            lastReturned = null;
        }

        @Override
        public void set(Byte e) {
            checkModification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            lastReturned.data = e;
        }

        @Override
        public void add(Byte e) {
            checkModification();
            NumberListImpl.this.add(currentIndex, e);
            currentIndex++;
            expectedSize++;
            lastReturned = null;
        }

        private void checkModification() {
            if (expectedSize != size) {
                throw new java.util.ConcurrentModificationException();
            }
        }
    }
}
