from type_checking_decorators import print_types
# import type_checking_decorators

# Test for simple decorator making introspection
_prompt = "==> " 
# As in the assignment text
@print_types
def foo(x:int, y, z:float) -> str:
    return str(x) + str(y) + str(z)
# Complete type hints: ints
@print_types
def add1(x:int,y:int) -> int:
    return x+y
# no parameter 1 and result type hints
@print_types
def add2(x:int,y):
    return x+y
# only strings
@print_types
def add3(x:str,y:str) -> str:
    return x+y
# function returning None
@print_types
def add4(x:str,y:str) -> str:
    print(_prompt + "add4 invoked: no return value")

if __name__ == '__main__':
    print(_prompt + "Invoking foo(2,' is ','similar to 2.0')...")
    print(_prompt + "foo(2,' is ','similar to 2.0')= "+ (str) (foo(2,' is ','similar to 2.0')))
    print(_prompt + "Invoking add1(2,3)...")
    print(_prompt + "add1(2,3)= "+ (str) (add1(2,3)))
    print(_prompt + "Invoking add2(2,3)...")
    print(_prompt + "add2(2,3)= "+ (str) (add2(2,3)))
    print(_prompt + "Invoking add3(2,3)...")
    print(_prompt + "add3(2,3)= "+ (str) (add3(2,3)))
    print(_prompt + "Invoking add3(2.0,3.14)...")
    print(_prompt + "add3(2.0,3.14)= "+ (str) (add3(2.0,3.14)))
    print(_prompt + "Invoking add3(2,3.14)...")
    print(_prompt + "add3(2,3.14)= "+ (str) (add3(2,3.14)))
    print(_prompt + "Invoking add3('hel','lo')...")
    print(_prompt + "add3('hel','lo')= "+ (str) (add3('hel','lo')))
    print(_prompt + "Invoking add4('hel','lo')...")
    print(_prompt + "add4('hel','lo')= "+ (str) (add4('hel','lo')))
