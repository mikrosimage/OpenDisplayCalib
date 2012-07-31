#ifndef CANNOTGETLUTEXCEPTION_H_
#define CANNOTGETLUTEXCEPTION_H_

#include "ColorRamDacException.h"
#include <iostream>

class CannotGetLutException : public ColorRamDacException
{
	std::string _message;
public:
	CannotGetLutException(std::string message);
	virtual ~CannotGetLutException() throw() ;
	const std::string what() const throw();
};

#endif /*CANNOTGETLUTEXCEPTION_H_*/
