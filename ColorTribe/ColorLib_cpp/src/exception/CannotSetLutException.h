#ifndef CANNOTSETLUTEXCEPTION_H_
#define CANNOTSETLUTEXCEPTION_H_

#include "ColorRamDacException.h"
#include <iostream>

class CannotSetLutException : public ColorRamDacException
{
	std::string _message;
public:
	CannotSetLutException(std::string message);
	virtual ~CannotSetLutException() throw ();
	const std::string what() const throw();
};

#endif /*CANNOTSETLUTEXCEPTION_H_*/
