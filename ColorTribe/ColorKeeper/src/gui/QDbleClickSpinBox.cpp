/*
 * QDbleClickSpinBox.cpp
 *
 *
 * Author: mfe
 */

#include "QDbleClickSpinBox.h"
#include "QDbleClickLineEdit.h"
#include <QDoubleSpinBox>
#include <QMouseEvent>
#include <QString>

QDbleClickSpinBox::QDbleClickSpinBox(const float &defaultValue) :
		QDoubleSpinBox() {
	setLineEdit(new QDbleClickLineEdit(defaultValue));
}

QDbleClickSpinBox::~QDbleClickSpinBox() {
}

