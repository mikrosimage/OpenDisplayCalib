/**
 * QDbleClickLineEdit.h
 *
 * Author: mfe
 */

#ifndef QDbleClickLineEdit_H_
#define QDbleClickLineEdit_H_

#include <QLineEdit>
class QObject;
class QEvent;

class QDbleClickLineEdit: public QLineEdit {
	Q_OBJECT

public:
	QDbleClickLineEdit(const float & defaultValue);
	virtual ~QDbleClickLineEdit();
	bool eventFilter(QObject* o, QEvent* e);
private:
	const float _defaultValue;

};

#endif /* QDbleClickLineEdit_H_ */
