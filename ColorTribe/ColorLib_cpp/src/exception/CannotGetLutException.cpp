#include "CannotGetLutException.h"
#include <iostream>
using namespace std;

CannotGetLutException::CannotGetLutException(string message)
{
	_message = "Get LUT error : ";
	_message.append(message);
}

CannotGetLutException::~CannotGetLutException() throw()
{
}

const string CannotGetLutException::what() const throw()
{
  return _message;
}
