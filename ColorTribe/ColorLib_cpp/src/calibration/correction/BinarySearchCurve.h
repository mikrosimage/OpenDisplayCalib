/*
 * BinarySearchCurve.h
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#ifndef BINARYSEARCHCURVE_H_
#define BINARYSEARCHCURVE_H_

#include <map>
#include <vector>
#include "Point2D.h"
#include "../../exception/BinarySearchCurveException.h"

class BinarySearchCurve {
	std::map<float, float> _points;
public:
	BinarySearchCurve();
	virtual ~BinarySearchCurve();

	BinarySearchCurve(std::vector<Point2D> &points);

	void put(const float &x, const float &y);
	float getValue(const float &x) throw (BinarySearchCurveException);
	float binarySearch(const  float &y, const float &epsilon,  float &xMin,  float &xMax);
	void checkMonotonic() throw (BinarySearchCurveException) ;
};

#endif /* BINARYSEARCHCURVE_H_ */
