/*
 * QDbleClickLineEdit.cpp
 *
 *
 * Author: mfe
 */

#include "QDbleClickLineEdit.h"
#include <QLineEdit>
#include <QMouseEvent>
#include <QString>

QDbleClickLineEdit::QDbleClickLineEdit(const float &defaultValue) :
		QLineEdit(), _defaultValue(defaultValue) {
	installEventFilter(this);
}

QDbleClickLineEdit::~QDbleClickLineEdit() {
}

bool QDbleClickLineEdit::eventFilter(QObject*, QEvent* e) {
	if (e->type() == QEvent::MouseButtonDblClick) {
//		setValue(0.0f);
		setText(QString::number(_defaultValue));

	}
// standard event processing
	return false;
}

