package com.palmtreesoftware.experimentandroid5_1

class LongRangeSet : MutableSet<Long> {
    private val mapIndexedByFirst = mutableMapOf<Long, LongRange>()
    private val mapIndexedByLast = mutableMapOf<Long, LongRange>()

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
                mapIndexedByFirst.remove(foundRangeUpperBoundary.first)
                mapIndexedByFirst.remove(foundRangeLowerBoundary.first)
                mapIndexedByLast.remove(foundRangeUpperBoundary.last)
                mapIndexedByLast.remove(foundRangeLowerBoundary.last)
                // 二つの Range を結合して map に追加する
                (foundRangeUpperBoundary.first..foundRangeLowerBoundary.last).also {
                    mapIndexedByFirst[it.first] = it
                    mapIndexedByLast[it.last] = it
                }
                return true
            } else {
                // element の前で終わる Range が存在し、element の次から始まる Range が存在しない場合
                // 見つかった Range を map から削除する
                mapIndexedByFirst.remove(foundRangeUpperBoundary.first)
                mapIndexedByLast.remove(foundRangeUpperBoundary.last)
                // 新たな Rangeを map に追加する
                (foundRangeUpperBoundary.first..element).also {
                    mapIndexedByFirst[it.first] = it
                    mapIndexedByLast[it.last] = it
                }
                return true
            }
        } else if (foundRangeLowerBoundary != null) {
            // element の前で終わる Range が存在せず、element の次から始まる Range が存在する場合
            // 見つかった Range を map から削除する
            mapIndexedByFirst.remove(foundRangeLowerBoundary.first)
            mapIndexedByLast.remove(foundRangeLowerBoundary.last)
            // 新たな Rangeを map に追加する
            (element..foundRangeLowerBoundary.last).also {
                mapIndexedByFirst[it.first] = it
                mapIndexedByLast[it.last] = it
            }
            return true
        } else {
            // element の前で終わる Range が存在せず、element の次から始まる Range も存在しない場合
            // 新たな Rangeを map に追加する
            (element..element).also {
                mapIndexedByFirst[it.first] = it
                mapIndexedByLast[it.last] = it
            }
            return true
        }
    }

    // Bulk Modification Operations
    /**
     * Adds all of the elements of the specified collection to this collection.
     *
     * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
     */
    override fun addAll(elements: Collection<Long>): Boolean {
        var modified = false
        elements.forEach {
            if (add(it))
                modified = true
        }
        return modified
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
    override fun iterator(): MutableIterator<Long> {
        return object : MutableIterator<Long> {
            private val source =
                mapIndexedByFirst.values
                    .flatten()
                    .toTypedArray()
                    .iterator()

            private var currentElement: Long? = null

            /**
             * Returns `true` if the iteration has more elements.
             */
            override fun hasNext(): Boolean {
                return source.hasNext()
            }

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
            override fun remove() {
                currentElement.also {
                    if (it == null)
                        throw Exception("")
                    remove(it)
                }
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
        mapIndexedByFirst.remove(foundRange.first)
        mapIndexedByLast.remove(foundRange.last)
        if (foundRange.count() > 1) {
            // foundRange に要素が複数あった場合、 element を除いた要素を map に追加する
            when (element) {
                foundRange.first -> {
                    // foundRange の先頭の要素が element だった場合
                    // foundRange から 先頭の要素を除いた Range を map に追加する
                    (foundRange.first + 1..foundRange.last).also {
                        mapIndexedByFirst[it.first] = it
                        mapIndexedByLast[it.last] = it
                    }
                }
                foundRange.last -> {
                    // foundRange の最後の要素が element だった場合
                    // foundRange から最後の要素を除いた Range を map に追加する
                    (foundRange.first until foundRange.last).also {
                        mapIndexedByFirst[it.first] = it
                        mapIndexedByLast[it.last] = it
                    }
                }
                else -> {
                    // foundRange の最初と最後以外の要素が element だった場合
                    // foundRange から element を除いた二つの Range を map に追加する
                    arrayOf(
                        foundRange.first until element,
                        element + 1..foundRange.last
                    ).forEach {
                        mapIndexedByFirst[it.first] = it
                        mapIndexedByLast[it.last] = it
                    }
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
        elements.forEach {
            if (remove(it))
                modified = true
        }
        return modified
    }

    /**
     * Retains only the elements in this collection that are contained in the specified collection.
     *
     * @return `true` if any element was removed from the collection, `false` if the collection was not modified.
     */
    override fun retainAll(elements: Collection<Long>): Boolean {
        val mapOfElements = elements.map { Pair(it, it) }.toMap()
        var modified = false
        mapIndexedByFirst.values
            .flatten()
            .toTypedArray()
            .forEach {
                if (!mapOfElements.containsKey(it)) {
                    remove(it)
                    modified = true
                }
            }
        return modified
    }

    override val size: Int
        get() = mapIndexedByFirst.values.sumBy { it.count() }

    override fun contains(element: Long): Boolean {
        return mapIndexedByFirst.values.any { it.contains(element) }
    }

    override fun containsAll(elements: Collection<Long>): Boolean {
        return elements.all { element -> mapIndexedByFirst.values.any { it.contains(element) } }
    }

    override fun isEmpty(): Boolean {
        return mapIndexedByFirst.isEmpty()
    }
}