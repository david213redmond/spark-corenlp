## Stanford CoreNLP wrapper for Apache Spark

This package wraps [Stanford CoreNLP](http://stanfordnlp.github.io/CoreNLP/) annotators as Spark
DataFrame functions following the [simple APIs](http://stanfordnlp.github.io/CoreNLP/simple.html)
introduced in Stanford CoreNLP 3.6.0.

This package requires Java 8 and CoreNLP 3.6.0 to run.
Users must include CoreNLP model jars as dependencies to use language models.

需要在 Spark 環境下進行中文斷詞可使用 segment 及 segment2 annotations.

segment annotation 是由 Stanford CoreNLP Library 的 CRF Classifier 組成.

segment2 annotation 是由 huaban/jieba-analysis 裡提供的 Jieba Segmenter 組成.


All functions are defined under `com.databricks.spark.corenlp.functions`.

* *`cleanxml`*: Cleans XML tags in a document and returns the cleaned document.
* *`tokenize`*: Tokenizes a sentence into words.
* *`ssplit`*: Splits a document into sentences.
* *`pos`*: Generates the part of speech tags of the sentence.
* *`lemma`*: Generates the word lemmas of the sentence.
* *`ner`*: Generates the named entity tags of the sentence.
* *`depparse`*: Generates the semantic dependencies of the sentence and returns a flattened list of
  `(source, sourceIndex, relation, target, targetIndex, weight)` relation tuples.
* *`coref`*: Generates the coref chains in the document and returns a list of
  `(rep, mentions)` chain tuples, where `mentions` are in the format of
  `(sentNum, startIndex, mention)`.
* *`natlog`*: Generates the Natural Logic notion of polarity for each token in a sentence, returned
  as `up`, `down`, or `flat`.
* *`openie`*: Generates a list of Open IE triples as flat `(subject, relation, target, confidence)`
  tuples.
* *`sentiment`*: Measures the sentiment of an input sentence on a scale of 0 (strong negative) to 4
  (strong positive).
* *`segment`*: Segments a sentence or document into phrases using the CRF Classifier Chinese Segmenter based on Pi-Chuan Chang's CRF Segmenter implementation.
* *`segment2`*: Segments a sentence or document into phrases using the Jieba Chinese Segmenter.

Users can chain the functions to create pipeline, for example:

~~~scala
import org.apache.spark.sql.functions._
import com.databricks.spark.corenlp.functions._

import sqlContext.implicits._

val input = Seq(
  (1, "<xml>Stanford University is located in California. It is a great university.</xml>")
).toDF("id", "text")

val output = input
  .select(cleanxml('text).as('doc))
  .select(explode(ssplit('doc)).as('sen))
  .select('sen, tokenize('sen).as('words), ner('sen).as('nerTags), sentiment('sen).as('sentiment))

output.show(truncate = false)
~~~

~~~
+----------------------------------------------+------------------------------------------------------+--------------------------------------------------+---------+
|sen                                           |words                                                 |nerTags                                           |sentiment|
+----------------------------------------------+------------------------------------------------------+--------------------------------------------------+---------+
|Stanford University is located in California .|[Stanford, University, is, located, in, California, .]|[ORGANIZATION, ORGANIZATION, O, O, O, LOCATION, O]|1        |
|It is a great university .                    |[It, is, a, great, university, .]                     |[O, O, O, O, O, O]                                |4        |
+----------------------------------------------+------------------------------------------------------+--------------------------------------------------+---------+
~~~

~~~
// With CRF Classifier Segmenter
val input1 = Seq(
  (1, "<xml>諾亞聖誕老人馬克杯 228ml</xml>")
).toDF("id", "text")

val output1 = input
  .select(cleanxml('text).as('doc))
  .select('doc, segment('doc).as('phrases))

output1.show(truncate = false)
~~~

~~~
+---------------------+------------------------------+
|doc                  |phrases                       |
+---------------------+------------------------------+
|諾亞聖誕老人馬克杯 228ml|[諾亞, 聖誕, 老人, 馬克杯, 228ml]|
+---------------------+------------------------------+
~~~

~~~
// With Jieba Segmenter
val input2 = Seq(
  (1, "<xml>土耳其驚爆兩罹難 警正搜捕另一攻擊者</xml>")
).toDF("id", "text")

val output2 = input
  .select(cleanxml('text).as('doc))
  .select('doc, segment2('doc).as('phrases))

output2.show(truncate = false)
~~~

~~~
+-----------------------------+----------------------------------------------------+
|doc                          |phrases                                             |
+-----------------------------+----------------------------------------------------+
|土耳其驚爆兩罹難 警正搜捕另一攻擊者|[土耳其, 驚爆, 兩, 罹難, " ", 警正, 搜捕, 另, 一, 攻擊者]|
+-----------------------------+----------------------------------------------------+
~~~

### Acknowledgements

Many thanks to Jason Bolton from the Stanford NLP Group for API discussions.
