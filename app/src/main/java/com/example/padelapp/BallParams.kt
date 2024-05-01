package com.example.padelapp

/*We'll use this class, BallParams, to define the parameters of the thrown  balls
We'll send an object of this class through a JSON file via the TCP socket

This way we can assure if in the past we want to add a new parameter to the
thrown balls we can just adjusting this class and including it easily*/
class BallParams(
    val numBalls: Int,  //Number of balls we'll be thrown
    val speed: Double,  //Ball's speed
    val height: Double, //Height from where the balls are thrown
    val spin: Int?      //Ball's spin to get curve effect
){

}