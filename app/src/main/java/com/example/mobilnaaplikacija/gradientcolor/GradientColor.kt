package com.example.mobilnaaplikacija.gradientcolor

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import kotlin.random.Random

object GradientColor {

    private val gradientList= mutableListOf<GradientDrawable>()

    init{
        gradientList.add(createGradient(Color.parseColor("#134E5E"),Color.parseColor("#71B280"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#614385"),Color.parseColor("#516395"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#16222A"),Color.parseColor("#3A6073"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#1D976C"),Color.parseColor("#93F9B9"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#1A2980"),Color.parseColor("#26D0CE"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#AA076B"),Color.parseColor("#61045F"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#3CA55C"),Color.parseColor("#B5AC49"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#02AAB0"),Color.parseColor("#00CDAC"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#603813"),Color.parseColor("#b29f94"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#e52d27"),Color.parseColor("#b31217"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#314755"),Color.parseColor("#26a0da"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#cc2b5e"),Color.parseColor("#753a88"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#1488CC"),Color.parseColor("#2B32B2"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#00467F"),Color.parseColor("#A5CC82"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#B79891"),Color.parseColor("#94716B"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#536976"),Color.parseColor("#292E49"),GradientDrawable.Orientation.LEFT_RIGHT))
        gradientList.add(createGradient(Color.parseColor("#FFE000"),Color.parseColor("#799F0C"),GradientDrawable.Orientation.LEFT_RIGHT))


    }


    private fun createGradient(startColor: Int, endColor: Int,orientation: Orientation): GradientDrawable {
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(startColor,endColor))
        gradientDrawable.cornerRadius = 100f
        return gradientDrawable
    }

    fun getRandomGradient(): GradientDrawable {
        val index = Random.nextInt(gradientList.size)
        return gradientList[index]
    }

    fun getRandomIndex(): Int{
        return Random.nextInt(gradientList.size)
    }

    fun getGradient(ind:Int): GradientDrawable{
        if(ind<0 || ind>= gradientList.size)return getRandomGradient()
        return gradientList[ind]
    }

}

