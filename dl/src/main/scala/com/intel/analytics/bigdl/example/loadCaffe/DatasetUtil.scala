/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.analytics.bigdl.example.loadCaffe

import java.nio.file.Path

import com.intel.analytics.bigdl.dataset._
import com.intel.analytics.bigdl.dataset.image.{BGRImgCropper, BGRImgNormalizer, _}
import com.intel.analytics.bigdl.tensor.Tensor
import com.intel.analytics.bigdl.utils.File

object Preprocessor {
  def apply(path: Path, imageSize: Int, batchSize: Int,
    transformers: Transformer[LabeledImageLocalPath, LabeledBGRImage])
  : LocalDataSet[Batch[Float]] = {
    DataSet.ImageFolder.paths(path).transform(
      MTLabeledBGRImgToBatch(
        width = imageSize,
        height = imageSize,
        batchSize = batchSize,
        transformer = transformers, toRGB = false))
  }
}

object AlexNetPreprocessor {
  def apply(path: Path, imageSize: Int, batchSize: Int, meanFile: String)
  : LocalDataSet[Batch[Float]] = {
    val means = File.load[Tensor[Float]](meanFile)
    val transformers = (LocalImgReader(256, 256, normalize = 1f) -> BGRImgPixelNormalizer(means)
      -> BGRImgCropper(imageSize, imageSize, CropCenter))
    Preprocessor(path, imageSize, batchSize, transformers)
  }
}

object GoogleNetPreprocessor {
  def apply(path: Path, imageSize: Int, batchSize: Int): LocalDataSet[Batch[Float]] = {
    val transformers = (LocalImgReader(256, 256, normalize = 1f)
      -> BGRImgCropper(imageSize, imageSize, CropCenter)
      -> BGRImgNormalizer(123, 117, 104, 1, 1, 1))
    Preprocessor(path, imageSize, batchSize, transformers)
  }
}


