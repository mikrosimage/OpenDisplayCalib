/*
 * Point2D.h
 *
 *  Created on: 11 dec. 2008
 *      Author: mfe
 */

#ifndef POINT2D_H_
#define POINT2D_H_

class Point2D {
public:
	float _x, _y;
	Point2D();
	Point2D(const float& x, const float& y);
	Point2D(const Point2D&);
	virtual ~Point2D();

	bool operator==(Point2D&);

	void absolute();
	void absolute(const Point2D&);
	void add(const Point2D &p1);
	void add(const Point2D &p1, const Point2D &p2);
	void clamp(const float &min, const float &max);
	void clamp(const float &min, const float &max, const Point2D &p);
	void clampMax(const float &max);
	void clampMax(const float &max, const Point2D &p);
	void clampMin(const float &min);
	void clampMin(const float &min, const Point2D &p);
	bool epsilonEquals(const Point2D &p1, const float &epsilon);
	void interpolate(const Point2D &p1, const float &alpha);
	void interpolate(const Point2D &p1, const Point2D &p2, const float &alpha);
	void negate();
	void negate(const Point2D &p1);
	void scale(const float &s);
	void scale(const float s, const Point2D &p1);
	void scaleAdd(const float &s, const Point2D &p1);
	void scaleAdd(const float &s, const Point2D &p1, const Point2D &p2);
	void sub(const Point2D &p1);
	void sub(const Point2D &p1, const Point2D &p2);

};

#endif /* POINT2D_H_ */

