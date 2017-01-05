package com.databricks.spark.corenlp

import scala.reflect.runtime.universe.TypeTag

import com.databricks.spark.corenlp.functions._

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._

class functionsSuite extends SparkFunSuite {

  private val sentence1 = "Stanford University is located in California."
  private val sentence2 = "It is a great university."
  private val sentence3 = "諾亞聖誕老人馬克杯 228ml"
  private val sentence4 = "土耳其驚爆兩罹難 警正搜捕另一攻擊者"
  private val document = s"$sentence1 $sentence2"
  private val xml = s"<xml><p>$sentence1</p><p>$sentence2</p></xml>"

  private def testFunction[T: TypeTag](function: UserDefinedFunction, input: T, expected: Any): Unit = {
    val df = sqlContext.createDataFrame(Seq((0, input))).toDF("id", "input")
    val actual = df.select(function(col("input"))).first().get(0)
    assert(actual === expected)
  }

  test("ssplit") {
    testFunction(ssplit, document, Seq(sentence1, sentence2))
  }

  test("tokenize") {
    val expected = Seq("Stanford", "University", "is", "located", "in", "California", ".")
    testFunction(tokenize, sentence1, expected)
  }

  test("pos") {
    val expected = Seq("NNP", "NNP", "VBZ", "JJ", "IN", "NNP", ".")
    testFunction(pos, sentence1, expected)
  }

  test("lemma") {
    val expected = Seq("Stanford", "University", "be", "located", "in", "California", ".")
    testFunction(lemma, sentence1, expected)
  }

  test("ner") {
    val expected = Seq("ORGANIZATION", "ORGANIZATION", "O", "O", "O", "LOCATION", "O")
    testFunction(ner, sentence1, expected)
  }

  test("natlog") {
    val expected = Seq("up", "down", "up", "up", "up", "up", "up")
    testFunction(natlog, sentence1, expected)
  }

  test("cleanxml") {
    val expected = "Stanford University is located in California . It is a great university ."
    testFunction(cleanxml, xml, expected)
  }

  ignore("coref") { // ignoring this because it is slow and uses lot of ram
    val expected = Seq(
      Row("Stanford University",
        Seq(
          Row(1, 1, "Stanford University"),
          Row(2, 1, "It"))))
    testFunction(coref, document, expected)
  }

  test("depparse") {
    val expected = Seq(
      Row("University", 2, "compound", "Stanford", 1, 1.0),
      Row("located", 4, "nsubj", "University", 2, 1.0),
      Row("located", 4, "cop", "is", 3, 1.0),
      Row("California", 6, "case", "in", 5, 1.0),
      Row("located", 4, "nmod:in", "California", 6, 1.0),
      Row("located", 4, "punct", ".", 7, 1.0))
    testFunction(depparse, sentence1, expected)
  }

  test("openie") {
    val expected = Seq(
      Row("Stanford University", "is", "located", 1.0),
      Row("Stanford University", "is located in", "California", 1.0))
    testFunction(openie, sentence1, expected)
  }

  test("sentiment") {
    testFunction(sentiment, sentence1, 1)
    testFunction(sentiment, sentence2, 4)
    testFunction(sentiment, document, 1) // only look at the first sentence
  }
  test("segment") {
    // val expected = Seq("諾亞聖誕", "老人", "馬克杯", "228ml") // when using PKU in model.
    val expected = Seq("諾亞", "聖誕", "老人", "馬克杯", "228ml") // when using CTB in model.
    testFunction(segment, sentence3, expected)
  }
  test("segment2") {
    val expected = Seq("土耳其", "驚爆", "兩", "罹難", " ", "警正", "搜捕", "另", "一", "攻擊者")
    testFunction(segment2, sentence4, expected)
  }
}
