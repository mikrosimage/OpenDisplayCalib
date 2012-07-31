/*
 * QDoubleClickedSlider.h
 *
 *  Created on: 23 mars 2009
 *      Author: mfe
 */

#ifndef QDOUBLECLICKEDSLIDER_H_
#define QDOUBLECLICKEDSLIDER_H_

#include <QSlider>
#include <QLabel>
class QObject;
class QEvent;
class QDoubleSpinBox;

class QGammaSlider : public QSlider {
	Q_OBJECT
private:

	unsigned int _OSScreenIndex;
	QDoubleSpinBox * _slidLab;
	bool _shouldUpdateModel;
	QGammaSlider(){}
public:
	QGammaSlider(unsigned int OSScreenIndex, QDoubleSpinBox *slidLab);
	virtual ~QGammaSlider();
	bool eventFilter(QObject* o, QEvent* e);
	static float getGammaValue(int value);
	static int getSliderValue(float value);
private slots:
void setValue(int value);
public slots:
void setGamma(unsigned int osScreenID, float gamma);
};

#endif /* QDOUBLECLICKEDSLIDER_H_ */
