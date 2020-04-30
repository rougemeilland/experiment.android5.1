package com.palmtreesoftware.experimentandroid5_1

class LongRangeSet() : MutableSet<Long> {
    private val mapIndexedByFirst: MutableMap<Long, LongRange> = mutableMapOf()
    private val mapIndexedByLast: MutableMap<Long, LongRange> = mutableMapOf()

    constructor(vararg values: Long) : this() {
        values.forEach {
            add(it)
        }
    }

    constructor(vararg values: ClosedRange<Long>) : this() {
        values.forEach { range ->
            addAll(range)
        }
    }

    /**
     * Adds the specified element to the set.
     *
     * @return `true` if the element has been added, `false` if the element is already contained in the set.
     */
    override fun add(element: Long): Boolean {
        // element が既に map に存在するか調べる
        if (mapIndexedByFirst.values.any { it.contains(element) }) {
            // element が既に map に存在していれば false を返す
            return false
        }
        val foundRangeUpperBoundary = mapIndexedByLast[element - 1] // element の前で終わる Range
        val foundRangeLowerBoundary = mapIndexedByFirst[element + 1] // element の次から始まる Range
        if (foundRangeUpperBoundary != null) {
            // element の前で終わる Range が存在する場合
            if (foundRangeLowerBoundary != null) {
                // element の前で終わる Range が存在し、element の次から始まる Range も存在する場合
                // 見つかった Range を map から削除する
                removeItem(foundRangeUpperBoundary)
                removeItem(foundRangeLowerBoundary)
                // 二つの Range を結合して map に追加する
                addItem(foundRangeUpperBoundary.first..foundRangeLowerBoundary.last)
                return true
            } else {
                // element の前で終わる Range が存在し、element の次から始まる Range が存在しない場合
                // 見つかった Range を map から削除する
                removeItem(foundRangeUpperBoundary)
                // 新たな Rangeを map に追加する
                addItem(foundRangeUpperBoundary.first..element)
                return true
            }
        } else if (foundRangeLowerBoundary != null) {
            // element の前で終わる Range が存在せず、element の次から始まる Range が存在する場合
            // 見つかった Range を map から削除する
            removeItem(foundRangeLowerBoundary)
            // 新たな Rangeを map に追加する
            addItem(element..foundRangeLowerBoundary.last)
            return true
        } else {
            // element の前で終わる Range が存在せず、element の次から始まる Range も存在しない場合
            // 新たな Rangeを map に追加する
            addItem(element..element)
            return true
        }
    }

    /**
     * Adds all of the elements of the specified collection to this collection.
     *
     * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
     */
    override fun addAll(elements: Collection<Long>): Boolean {
        var modified = false
        if (elements is LongRangeSet) {
            elements.mapIndexedByFirst.values.forEach {
                if (addAll(it as ClosedRange<Long>))
                    modified = true
            }
        } else {
            elements.forEach {
                if (add(it))
                    modified = true
            }
        }
        return modified
    }

    /**
     * Adds all of the elements of the specified collection to this collection.
     *
     * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
     */
    fun addAll(elements: ClosedRange<Long>): Boolean {
        // elements と完全一致する要素が存在した場合は false で復帰する
        if (mapIndexedByFirst[elements.start].let { it != null && it.last == elements.endInclusive })
            return false
        // elements と共通項を持つ要素を探す
        val intersectedElements =
            mapIndexedByFirst.values.filter { it.isIntersectedRange(elements) }.toTypedArray()
        // elements と共通項を持つ要素を map から削除する
        intersectedElements.forEach { removeItem(it) }
        // 新たに追加する要素を作成する
        intersectedElements.fold(elements.toLongRange()) { value, element ->
            value.unionRange(element).let {
                // element には value と共通項を持つ Range しか渡されないはずなので、 it.size は必ず 1 になる
                if (it.size != 1)
                    throw Exception("${it.javaClass.canonicalName}.allAll(): Internal error")
                else
                    it[0]
            }
        }.also { addItem(it) }
        return true
    }

    /**
     * Removes all elements from this collection.
     */
    override fun clear() {
        mapIndexedByFirst.clear()
        mapIndexedByLast.clear()
    }

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): MutableIterator<Long> =
        object : MutableIterator<Long> {
            private val source =
                mapIndexedByFirst.values
                    .flatten()
                    .toList()
                    .iterator()

            private var currentElement: Long? = null

            /**
             * Returns `true` if the iteration has more elements.
             */
            /**
             * Returns `true` if the iteration has more elements.
             */
            override fun hasNext(): Boolean {
                return source.hasNext()
            }

            /**
             * Returns the next element in the iteration.
             */
            /**
             * Returns the next element in the iteration.
             */
            override fun next(): Long {
                return source.next().also {
                    currentElement = it
                }
            }

            /**
             * Removes from the underlying collection the last element returned by this iterator.
             */
            /**
             * Removes from the underlying collection the last element returned by this iterator.
             */
            override fun remove() {
                currentElement.also {
                    if (it == null)
                        throw IllegalStateException()
                    remove(it)
                }
            }

        }

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present.
     *
     * @return `true` if the element has been successfully removed; `false` if it was not present in the collection.
     */
    override fun remove(element: Long): Boolean {
        val foundRange =
            mapIndexedByFirst.values.firstOrNull { it.contains(element) }
                ?: return false
        // map から foundRange を削除する
        removeItem(foundRange)
        if (foundRange.count() > 1) {
            // foundRange に要素が複数あった場合、 element を除いた要素を map に追加する
            when (element) {
                foundRange.first -> {
                    // foundRange の先頭の要素が element だった場合
                    // foundRange から 先頭の要素を除いた Range を map に追加する
                    addItem(foundRange.first + 1..foundRange.last)
                }
                foundRange.last -> {
                    // foundRange の最後の要素が element だった場合
                    // foundRange から最後の要素を除いた Range を map に追加する
                    addItem(foundRange.first until foundRange.last)
                }
                else -> {
                    // foundRange の最初と最後以外の要素が element だった場合
                    // foundRange から element を除いた二つの Range を map に追加する
                    arrayOf(
                        foundRange.first until element,
                        element + 1..foundRange.last
                    ).forEach { addItem(it) }
                }
            }
        }
        return true
    }

    /**
     * Removes all of this collection's elements that are also contained in the specified collection.
     *
     * @return `true` if any of the specified elements was removed from the collection, `false` if the collection was not modified.
     */
    override fun removeAll(elements: Collection<Long>): Boolean {
        var modified = false
        if (elements is LongRangeSet) {
            elements.mapIndexedByFirst.values.forEach {
                if (removeAll(it as ClosedRange<Long>))
                    modified = true
            }
        } else {
            elements.forEach {
                if (remove(it))
                    modified = true
            }
        }
        return modified
    }

    /**
     * Removes all of this collection's elements that are also contained in the specified collection.
     *
     * @return `true` if any of the specified elements was removed from the collection, `false` if the collection was not modified.
     */
    fun removeAll(elements: ClosedRange<Long>): Boolean {
        // elements と共通項を持つ要素を探す
        val intersectedElements =
            mapIndexedByFirst.values
                .filter { it.isIntersectedRange(elements) }
                .toList()
        // elements と共通項を持つ要素が存在しなければ false を返す
        if (intersectedElements.isEmpty())
            return false
        // elements と共通項を持つ要素を map から削除する
        intersectedElements.forEach { removeItem(it) }
        // 新たに追加する要素を作成して map に追加する
        intersectedElements
            .map { it.differenceRange(elements) }
            .flatten()
            .forEach { addItem(it) }
        return true
    }

    /**
     * Retains only the elements in this collection that are contained in the specified collection.
     *
     * @return `true` if any element was removed from the collection, `false` if the collection was not modified.
     */
    override fun retainAll(elements: Collection<Long>): Boolean {
        var modified = false
        if (elements is LongRangeSet) {
            if (mapIndexedByFirst.values.all { elements.containsAll(it) })
                return false
            mapIndexedByFirst.values
                .crossMap(elements.mapIndexedByFirst.values) { x, y -> x.intersectionRange(y) }
                .filter { it.isNotEmpty() }
                .toList()
                .also { clear() }
                .forEach { addItem(it) }
        } else {
            val mapOfElements = elements.map { Pair(it, it) }.toMap()
            mapIndexedByFirst.values
                .flatten()
                .toTypedArray()
                .forEach {
                    if (!mapOfElements.containsKey(it)) {
                        remove(it)
                        modified = true
                    }
                }
        }
        return modified
    }

    /**
     * Retains only the elements in this collection that are contained in the specified collection.
     *
     * @return `true` if any element was removed from the collection, `false` if the collection was not modified.
     */
    fun retainAll(elements: ClosedRange<Long>): Boolean {
        var modified = false
        elements.toComplementRange().forEach {
            if (removeAll(it as ClosedRange<Long>))
                modified = true
        }
        return modified
    }

    /**
     * Returns the size of the collection.
     */
    override val size: Int
        get() =
            mapIndexedByFirst.values.sumBy { it.count() }

    /**
     * Checks if the specified element is contained in this collection.
     */
    override fun contains(element: Long): Boolean =
        mapIndexedByFirst.values.any { it.contains(element) }

    /**
     * Checks if all elements in the specified collection are contained in this collection.
     */
    override fun containsAll(elements: Collection<Long>): Boolean =
        if (elements is LongRangeSet) {
            elements.mapIndexedByFirst.values.all { containsAll(it as ClosedRange<Long>) }
        } else {
            elements.all { element -> mapIndexedByFirst.values.any { it.contains(element) } }
        }

    fun containsAll(elements: ClosedRange<Long>): Boolean =
        mapIndexedByFirst[elements.start]
            .let { it != null && it.last == elements.endInclusive }

    /**
     * Returns `true` if the collection is empty (contains no elements), `false` otherwise.
     */
    override fun isEmpty(): Boolean =
        mapIndexedByFirst.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LongRangeSet
        if (!equals(mapIndexedByFirst, other.mapIndexedByFirst)) return false
        return true
    }

    override fun hashCode(): Int {
        return hashCode(mapIndexedByFirst)
    }

    override fun toString(): String =
        mapIndexedByFirst.values
            .joinToString(
                separator = ", ",
                prefix = "[",
                postfix = "]"
            ) {
                if (it.first == it.last)
                    it.first.toString()
                else
                    "${it.first}..${it.last}"
            }

    private fun addItem(element: LongRange) {
        if (element.isEmpty())
            throw Exception("internal exception: 'first' must be lesser than or equals 'last': first=${element.first}, last=${element.last}")
        mapIndexedByFirst[element.first] = element
        mapIndexedByLast[element.last] = element
    }

    private fun removeItem(element: LongRange) {
        mapIndexedByFirst.remove(element.first)
        mapIndexedByLast.remove(element.last)
    }

    private fun equals(x: MutableMap<Long, LongRange>, y: MutableMap<Long, LongRange>): Boolean =
        x.size == y.size &&
                x.all { keyValueOfX ->
                    val valueOfY = y[keyValueOfX.key]
                    valueOfY != null && valueOfY == keyValueOfX.value
                }

    private fun hashCode(x: MutableMap<Long, LongRange>): Int =
        // x.values の要素の順番に依存しないように計算する
        x.values.sumBy { it.hashCode() }
}