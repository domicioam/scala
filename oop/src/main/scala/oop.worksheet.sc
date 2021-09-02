import java.{util => ju}
abstract class MyList[+A] {
  def head: A
  def tail: MyList[A]
  def isEmpty: Boolean
  def add[B >: A](item: B): MyList[B]
  def printElements: String
  // higher-order functions
  def map[B](transformer: Function1[A, B]): MyList[B]
  def flatMap[B](transformer: Function1[A, MyList[B]]): MyList[B]
  def filter(predicate: Function1[A, Boolean]): MyList[A]
  def ++[B >: A](list: MyList[B]): MyList[B]
  override def toString: String = "[" + printElements + "]"
}

case object Empty extends MyList[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyList[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](item: B): MyList[B] = Cons(item, Empty)
  def map[B](transformer: Function1[Nothing, B]): MyList[B] = Empty
  def flatMap[B](transformer: Function1[Nothing, MyList[B]]): MyList[B] =
    Empty
  def filter(predicate: Function1[Nothing, Boolean]): MyList[Nothing] = Empty
  def ++[B >: Nothing](list: MyList[B]): MyList[B] = list
  override def printElements = ""
}

case class Cons[+A](h: A, t: MyList[A]) extends MyList[A] {
  def head: A = h
  def tail: MyList[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](item: B): MyList[B] = Cons(item, this)
  def map[B](transformer: Function1[A, B]): MyList[B] =
    Cons(transformer(h), t.map(transformer))
  def flatMap[B](transformer: Function1[A, MyList[B]]): MyList[B] =
    transformer(h) ++ t.flatMap(transformer)
  def filter(predicate: Function1[A, Boolean]): MyList[A] =
    if predicate(h) then Cons(h, t.filter(predicate))
    else t.filter(predicate)
  override def printElements: String =
    if t.isEmpty then "" + h
    else s"${h} ${t.printElements}"
  def ++[B >: A](list: MyList[B]): MyList[B] = Cons(h, t ++ list)
}

trait MyPredicate[-T] { // replaced with Function1
  def test(t: T): Boolean
}

trait MyTransformer[-A, B] { // replaced with Function1
  def transform(a: A): B
}

class Vehicle
class Car extends Vehicle

val list1: MyList[Vehicle] = new Cons[Car](new Car, Empty)
val list2: MyList[Vehicle] = new Cons[Vehicle](new Vehicle, Empty)
// var contravariance: MyList[Car] = new Cons[Vehicle](new Vehicle, Empty) // MyList[-A]
val ints: MyList[Int] = Cons[Int](1, Cons(2, Empty))

ints.map(new Function1[Int, Int] {
  override def apply(elem: Int): Int = elem * 2
})

ints.flatMap(new Function1[Int, MyList[Int]] {
  override def apply(elem: Int): MyList[Int] =
    Cons(elem, Cons(elem + 1, Empty))
})

val concat: ((String, String) => String) =
  new Function2[String, String, String] {
    override def apply(a: String, b: String): String = a + b
  }

concat("1", "2")
  
def myFunction(num: Int): Function1[Int, Int] = // higher-order function
  return new Function1[Int, Int] {
    override def apply(otherNum: Int): Int =
      num + otherNum
  }

val func = myFunction(1)
func(2)
myFunction(1)(2) // curry function
