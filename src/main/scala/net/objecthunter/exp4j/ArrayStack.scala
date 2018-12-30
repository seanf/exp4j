/*
 * Copyright 2015 Federico Vera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*
 * Copyright 2015 Federico Vera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.objecthunter.exp4j

import java.util.EmptyStackException

/**
  * Simple double stack using a double array as data storage
  *
  * @author Federico Vera (dktcoding [at] gmail)
  */
class ArrayStack private[exp4j](val initialCapacity: Int) {
  if (initialCapacity <= 0) throw new IllegalArgumentException("Stack's capacity must be positive")

  private var data:Array[Double] = new Array[Double](initialCapacity)
  private var idx = -1

  def this() {
    this(5)
  }

  private[exp4j] def push(value: Double) = {
    if (idx + 1 == data.length) {
      val temp = new Array[Double]((data.length * 1.2).toInt + 1)
      System.arraycopy(data, 0, temp, 0, data.length)
      data = temp
    }
    data({
      idx += 1; idx
    }) = value
  }

  private[exp4j] def peek = {
    if (idx == -1) throw new EmptyStackException
    data(idx)
  }

  private[exp4j] def pop = {
    if (idx == -1) throw new EmptyStackException
    data({
      idx -= 1; idx + 1
    })
  }

  private[exp4j] def isEmpty = idx == -1

  private[exp4j] def size = idx + 1
}
