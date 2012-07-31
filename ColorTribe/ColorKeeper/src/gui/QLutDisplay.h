/*
 * QLutDisplay.h
 *
 *  Created on: 20 mars 2009
 *      Author: mfe
 */

#ifndef QLUTDISPLAY_H_
#define QLUTDISPLAY_H_

#include <LUT2DCorrection.h>

#include <QGLWidget>

class QLutDisplay : public QGLWidget {
	Q_OBJECT
public:
	QLutDisplay(const LUT2DCorrection *  lut, const unsigned int screenIndex);
	virtual ~QLutDisplay();

	const LUT2DCorrection *_lut;
protected:
    void paintGL();
	void resizeGL( int w, int h );
	void initializeGL();
	void drawLUT();
	void setLut(const LUT2DCorrection *lut){_lut = lut;}
private:
	int			_width;
	int			_height;
	const unsigned int _OSScreenIndex;
private slots:
    void lutRepaint(unsigned int index);

};

#endif /* QLUTDISPLAY_H_ */
