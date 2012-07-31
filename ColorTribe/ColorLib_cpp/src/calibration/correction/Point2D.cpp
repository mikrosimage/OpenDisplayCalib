/*
 * Point2D.cpp
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#include "Point2D.h"
#include <math.h>
#include "CurveMath.h"
#include <algorithm>

Point2D::Point2D() :
	_x(0), _y(0) {

}

Point2D::Point2D(const float &x, const float &y) :
	_x(x), _y(y) {

}

Point2D::Point2D(const Point2D &point) :
	_x(point._x), _y(point._y) {

}

Point2D::~Point2D() {
}

bool Point2D::operator==(Point2D& p){
	return ((p._x == _x)&&(p._y == _y));
}

/**
 *Sets each component of this Point2D to its absolute value.
 */
void Point2D::absolute() {
	absolute(*this);
}

/**
 * Sets each component of the Point2D parameter to its absolute value and places the modified values into this Point2D.
 * @param p
 */
void Point2D::absolute(const Point2D &p) {
	_x = fabs(p._x);
	_y = fabs(p._y);
}

void Point2D::add(const Point2D &p1) {
	add(*this, p1);
}

/**
 *     Sets the value of this Point2D to the sum of tuples p1 and p2.
 * @param p1
 * @param p2
 */
void Point2D::add(const Point2D &p1, const Point2D &p2) {
	_x = p1._x + p2._x;
	_y = p1._y + p2._y;
}
/**
 * Clamps this Point2D to the range [low, high].
 * @param min
 * @param max
 */
void Point2D::clamp(const float &min, const float &max) {
	clamp(min, max, *this);
}

/**
 * Clamps the Point2D parameter to the range [low, high] and places the values into this Point2D.
 * @param min
 * @param max
 * @param p
 */
void Point2D::clamp(const float &min, const float &max, const Point2D &p) {
	_x = CurveMath::clamp(p._x, min, max);
	_y = CurveMath::clamp(p._y, min, max);
}

/**
 * Clamps the maximum value of this Point2D to the max parameter.
 * @param max
 */
void Point2D::clampMax(const float &max) {
	clampMax(max, *this);
}

/**
 * Clamps the maximum value of the Point2D parameter to the max parameter and places the values into this Point2D.
 * @param max
 * @param p
 */
void Point2D::clampMax(const float &max, const Point2D &p) {
	_x = CurveMath::clampMax(p._x, max);
	_y = CurveMath::clampMax(p._y, max);
}

/**
 * Clamps the minimum value of this Point2D to the min parameter.
 * @param min
 */
void Point2D::clampMin(const float &min) {
	clampMin(min, *this);
}

/**
 *  Clamps the minimum value of the Point2D parameter to the min parameter and places the values into this Point2D.
 * @param min
 * @param p
 */
void Point2D::clampMin(const float &min, const Point2D &p) {
	_x = CurveMath::clampMin(p._x, min);
	_y = CurveMath::clampMin(p._y, min);
}

/**
 * Returns true if the L-infinite distance between this Point2D and Point2D p1 is less than or equal to the epsilon parameter, otherwise returns false.
 * @param p1
 * @param epsilon
 * @return
 */
bool Point2D::epsilonEquals(const Point2D &p1, const float &epsilon) {
	return fabs(p1._x - _x) > fabs(p1._y - _y) ? fabs(p1._x - _x) : fabs(p1._y
			- _y) <= epsilon;
}

/**
 * Linearly interpolates between this Point2D and Point2D p1 and places the result into this Point2D: this = (1-alpha)*this + alpha*p1.
 * @param p1
 * @param alpha
 */
void Point2D::interpolate(const Point2D &p1, const float &alpha) {
	interpolate(*this, p1, alpha);
}

/**
 * Linearly interpolates between tuples p1 and p2 and places the result into this Point2D: this = (1-alpha)*p1 + alpha*p2.
 * @param p1
 * @param p2
 * @param alpha
 */
void Point2D::interpolate(const Point2D &p1, const Point2D &p2,
		const float &alpha) {
	_x = CurveMath::interpolate(p1._x, p2._x, alpha);
	_y = CurveMath::interpolate(p1._y, p2._y, alpha);
}

/**
 * Negates the value of this Point2D in place.
 */
void Point2D::negate() {
	negate(*this);
}

/**
 * Sets the value of this Point2D to the negation of Point2D p1.
 * @param p1
 */
void Point2D::negate(const Point2D &p1) {
	_x = -p1._x;
	_y = -p1._y;
}

/**
 * Sets the value of this Point2D to the scalar multiplication of itself.
 * @param s
 */
void Point2D::scale(const float &s) {
	scale(s, *this);
}

/**
 * Sets the value of this Point2D to the scalar multiplication of Point2D p1.
 * @param s
 * @param p1
 */
void Point2D::scale(const float s, const Point2D &p1) {
	_x = p1._x * s;
	_y = p1._y * s;
}

/**
 * Sets the value of this Point2D to the scalar multiplication of itself and then adds Point2D p1 (this = s*this + p1).
 * @param s
 * @param p1
 */
void Point2D::scaleAdd(const float &s, const Point2D &p1) {
	scaleAdd(s, *this, p1);
}

/**Sets the value of this Point2D to the scalar multiplication of Point2D p1 and then adds Point2D p2 (this = s*p1 + p2).
 * @param s
 * @param p1
 * @param p2
 */
void Point2D::scaleAdd(const float &s, const Point2D &p1, const Point2D &p2) {
	_x = s * p1._x + p2._x;
	_y = s * p1._y + p2._y;
}

/**
 * Sets the value of this Point2D to the difference of itself and p1 (this = this - p1).
 * @param p1
 */
void Point2D::sub(const Point2D &p1) {
	sub(*this, p1);
}

/**
 * Sets the value of this Point2D to the difference of tuples p1 and p2 (this = p1 - p2).
 * @param p1
 * @param p2
 */
void Point2D::sub(const Point2D &p1, const Point2D &p2) {
	_x = p1._x - p2._x;
	_y = p1._y - p2._y;
}
