package com.zonkey.simplemealplanner.network

interface NetworkChecker {
  fun internetIsAvailable(): Boolean
}