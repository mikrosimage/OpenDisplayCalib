/**
 * QDbleClickSpinBox.h
 *
 * Author: mfe
 */

#ifndef QDbleClickSpinBox_H_
#define QDbleClickSpinBox_H_

#include <QDoubleSpinBox>
#include <QLineEdit>
class QObject;
class QEvent;

class QDbleClickSpinBox: public QDoubleSpinBox {
	Q_OBJECT

public:
	QDbleClickSpinBox(const float &defaultValue);
	virtual ~QDbleClickSpinBox();

};

#endif /* QDbleClickSpinBox_H_ */
