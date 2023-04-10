package com.example.comebackhome.model

data class Route (val features: List<Feature>)
data class Feature(val geometry:Geometry)
data class Geometry(val coordinates:List<List<Double>>)