package com.junting.drug_android_frontend.model.cloud_vision

data class ImageAnnotateResponse(
    val responses: List<Response>
)

data class Response(
    val cropHintsAnnotation: CropHintsAnnotation,
    val fullTextAnnotation: FullTextAnnotation,
    val imagePropertiesAnnotation: ImagePropertiesAnnotation,
    val labelAnnotations: List<LabelAnnotation>,
    val logoAnnotations: List<LogoAnnotation>,
    val safeSearchAnnotation: SafeSearchAnnotation,
    val textAnnotations: List<TextAnnotation>
)

data class CropHintsAnnotation(
    val cropHints: List<CropHint>
)

data class FullTextAnnotation(
    val pages: List<Page>,
    val text: String
)

data class ImagePropertiesAnnotation(
    val dominantColors: DominantColors
)

data class LabelAnnotation(
    val description: String,
    val mid: String,
    val score: Double,
    val topicality: Double
)

data class LogoAnnotation(
    val boundingPoly: BoundingPoly,
    val description: String,
    val mid: String,
    val score: Double
)

data class SafeSearchAnnotation(
    val adult: String,
    val medical: String,
    val racy: String,
    val spoof: String,
    val violence: String
)

data class TextAnnotation(
    val boundingPoly: BoundingPoly,
    val description: String,
    val locale: String
)

data class CropHint(
    val boundingPoly: BoundingPoly,
    val confidence: Double,
    val importanceFraction: Int
)

data class BoundingPoly(
    val vertices: List<Vertice>
)

data class Vertice(
    val x: Int,
    val y: Int
)

data class Page(
    val blocks: List<Block>,
    val confidence: Double,
    val height: Int,
    val width: Int
)

data class Block(
    val blockType: String,
    val boundingBox: BoundingBox,
    val confidence: Double,
    val paragraphs: List<Paragraph>
)

data class BoundingBox(
    val vertices: List<Vertice>
)

data class Paragraph(
    val boundingBox: BoundingBox,
    val confidence: Double,
    val words: List<Word>
)

data class Word(
    val boundingBox: BoundingBox,
    val confidence: Double,
    val symbols: List<Symbol>
)

data class Symbol(
    val boundingBox: BoundingBox,
    val confidence: Double,
    val `property`: Property,
    val text: String
)

data class Property(
    val detectedBreak: DetectedBreak
)

data class DetectedBreak(
    val type: String
)

data class DominantColors(
    val colors: List<Color>
)

data class Color(
    val color: ColorX,
    val pixelFraction: Double,
    val score: Double
)

data class ColorX(
    val blue: Int,
    val green: Int,
    val red: Int
)
