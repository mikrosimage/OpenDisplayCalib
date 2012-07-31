/*
 * Math.h
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#ifndef MATH_H_
#define MATH_H_

class CurveMath {
public:
	CurveMath(){};
	virtual ~CurveMath(){};

	static float clampMax(const float &value, const float &limit);
	static float clampMin(const float &value, const float &limit);
	static float clamp(const float &value, const float &min, const float &max);
	static float interpolate(const float &a, const float &b, const float &alpha);

};

#endif /* MATH_H_ */
