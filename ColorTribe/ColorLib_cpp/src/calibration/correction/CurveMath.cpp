/*
 * Math.cpp
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#include "CurveMath.h"

float CurveMath::clampMax(const float &value, const float &limit) {
	return value > limit ? limit : value;
}

float CurveMath::clampMin(const float &value, const float &limit) {
	return value < limit ? limit : value;
}

float CurveMath::clamp(const float &value, const float &min, const float &max) {
	return clampMin(clampMax(value, max), min);
}

float CurveMath::interpolate(const float &a, const float &b, const float &alpha) {
	return (1 - alpha) * a + alpha * b;
}
