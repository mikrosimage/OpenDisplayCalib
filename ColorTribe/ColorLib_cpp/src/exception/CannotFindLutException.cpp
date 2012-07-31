#include "CannotFindLutException.h"
#include <iostream>
using namespace std;

CannotFindLutException::CannotFindLutException(const string path)
{

	_message.append("LUT file error : cannot find ");
	_message.append(path);
	
}

CannotFindLutException::~CannotFindLutException() throw ()
{
}

const string CannotFindLutException::what() const throw()
{
  return _message;
}
