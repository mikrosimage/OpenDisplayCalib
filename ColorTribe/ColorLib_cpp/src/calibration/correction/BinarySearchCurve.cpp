/*
 * BinarySearchCurve.cpp
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#include "BinarySearchCurve.h"
#include "../../exception/BinarySearchCurveException.h"
using namespace std;

BinarySearchCurve::BinarySearchCurve() {
}

BinarySearchCurve::BinarySearchCurve(vector<Point2D> &points) {
	vector<Point2D>::iterator it;
	for (it = points.begin(); it != points.end(); it++) {
		put((*it)._x, (*it)._y);
	}
}

BinarySearchCurve::~BinarySearchCurve() {
}

void BinarySearchCurve::put(const float &x, const float &y) {
	_points.insert(map<float, float>::value_type(x, y));
}

float BinarySearchCurve::getValue(const float &x) throw (BinarySearchCurveException){
	if (_points.size() < 2)
		throw BinarySearchCurveException("number of point < 2");

	map<float, float>::iterator it = _points.find(x);
	if (it != _points.end())
		return (*it).second;

	it = _points.begin();
	pair<float, float> previousValue((*it).first, (*it).second);
	it++;
	for (; it != _points.end(); it++) {
		if ((*it).first > x) {
			float alpha = (x - previousValue.first) / ((*it).first
					- previousValue.first);
			return alpha * (*it).second + (1 - alpha) * previousValue.second;
		}
		previousValue = (*it);
	}
	throw BinarySearchCurveException("value not found");
}

float BinarySearchCurve::binarySearch(const float &y, const float &epsilon,
		 float &xMin,  float &xMax) {
	float yMin = getValue(xMin);
	float yMax = getValue(xMax);
	while (true) {
		float mid = (xMax + xMin) / 2;
		float midValue = getValue(mid);
		if (y > midValue) {
			xMin = mid;
			yMin = midValue;
		} else {
			xMax = mid;
			yMax = midValue;
		}
		if (yMin == y)
			return xMin;
		if (yMax == y)
			return xMax;
		if (yMax - yMin < epsilon)
			return (xMax + xMin) / 2;
	}
}

void BinarySearchCurve::checkMonotonic() throw (BinarySearchCurveException){
	map<float, float>::iterator it = _points.begin();
	float previousValue = (*it).first;
	it++;
	for (; it != _points.end(); it++) {
		if ((*it).first <= previousValue)
			throw BinarySearchCurveException("Function is not monotonic");
		previousValue = (*it).first;
	}
}
