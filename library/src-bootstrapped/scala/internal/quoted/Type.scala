package scala.internal.quoted

import scala.quoted._
import scala.internal.tasty.CompilerInterface.quoteContextWithCompilerInterface

/** Quoted type (or kind) `T` backed by a tree */
final class Type[Tree](val typeTree: Tree, val scopeId: Int) extends scala.quoted.Type[Any] {
  override def equals(that: Any): Boolean = that match {
    case that: Type[_] => typeTree ==
      // TastyTreeExpr are wrappers around trees, therfore they are equals if their trees are equal.
      // All scopeId should be equal unless two different runs of the compiler created the trees.
      that.typeTree && scopeId == that.scopeId
    case _ => false
  }

  /** View this expression `quoted.Type[T]` as a `TypeTree` */
  def unseal(using qctx: QuoteContext): qctx.tasty.TypeTree =
    if (quoteContextWithCompilerInterface(qctx).tasty.compilerId != scopeId)
      throw new scala.quoted.ScopeException("Cannot call `scala.quoted.staging.run(...)` within a macro or another `run(...)`")
    typeTree.asInstanceOf[qctx.tasty.TypeTree]

  override def hashCode: Int = typeTree.hashCode
  override def toString: String = "'[ ... ]"
}

object Type {

  /** Pattern matches an the scrutineeType against the patternType and returns a tuple
   *  with the matched holes if successful.
   *
   *  Holes:
   *    - scala.internal.Quoted.patternHole[T]: hole that matches an expression `x` of type `Type[U]`
   *                                            if `U <:< T` and returns `x` as part of the match.
   *
   *  @param scrutineeType `Type[_]` on which we are pattern matching
   *  @param patternType `Type[_]` containing the pattern tree
   *  @param hasTypeSplices `Boolean` notify if the pattern has type splices (if so we use a GADT context)
   *  @param qctx the current QuoteContext
   *  @return None if it did not match, `Some(tup)` if it matched where `tup` contains `Type[Ti]``
   */
  def unapply[TypeBindings <: Tuple, Tup <: Tuple](scrutineeType: scala.quoted.Type[_])(using patternType: scala.quoted.Type[_],
        hasTypeSplices: Boolean, qctx: QuoteContext): Option[Tup] = {
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    val qctx2 = if hasTypeSplices then qctx1.tasty.Constraints_context else qctx1
    given qctx2.type = qctx2
    new Matcher.QuoteMatcher[qctx2.type](qctx2).typeTreeMatch(scrutineeType.unseal, patternType.unseal, hasTypeSplices).asInstanceOf[Option[Tup]]
  }

  def Unit: QuoteContext ?=> quoted.Type[Unit] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_UnitType.seal.asInstanceOf[quoted.Type[Unit]]


  def Boolean: QuoteContext ?=> quoted.Type[Boolean] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_BooleanType.seal.asInstanceOf[quoted.Type[Boolean]]


  def Byte: QuoteContext ?=> quoted.Type[Byte] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_ByteType.seal.asInstanceOf[quoted.Type[Byte]]


  def Char: QuoteContext ?=> quoted.Type[Char] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_CharType.seal.asInstanceOf[quoted.Type[Char]]


  def Short: QuoteContext ?=> quoted.Type[Short] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_ShortType.seal.asInstanceOf[quoted.Type[Short]]


  def Int: QuoteContext ?=> quoted.Type[Int] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_IntType.seal.asInstanceOf[quoted.Type[Int]]


  def Long: QuoteContext ?=> quoted.Type[Long] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_LongType.seal.asInstanceOf[quoted.Type[Long]]


  def Float: QuoteContext ?=> quoted.Type[Float] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_FloatType.seal.asInstanceOf[quoted.Type[Float]]


  def Double: QuoteContext ?=> quoted.Type[Double] =
    val qctx1 = quoteContextWithCompilerInterface(qctx)
    qctx1.tasty.Definitions_DoubleType.seal.asInstanceOf[quoted.Type[Double]]


}
