package com.databricks.spark.corenlp.properties

import java.io.FileInputStream
import java.util.Properties

package object StanfordCoreNLP_chinese_properties{
    // build up the properties for chinese segmenter.
    val props = new Properties()
    props.setProperty("segLoc","edu/stanford/nlp/models/segmenter/chinese/pku.gz")
    props.setProperty("sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese")
    props.setProperty("serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz")
    props.setProperty("inputEncoding", "UTF-8")
    props.setProperty("sighanPostProcessing", "true")
    
}
