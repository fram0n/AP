from type_checking_decorators import type_check
import sys

# Test for simple type inspector decorator
_prompt = "==> " 
# Example in Assignment text
@type_check
def foo(x:int, y, z:float) -> str:
    return str(x) + str(y) + str(z)
# Complete annotations: int
@type_check
def add1(x:int,y:int) -> int:
    return x+y
# No return type hint
@type_check
def add2(x:int,y:int):
    return x+y
# No type hint for parameter 1
@type_check
def add3(x:float,y) -> float:
    return x+y
# Type unions as hints
@type_check
def add4(x:float|int|str ,y:int|str) -> float|int:
    return x+y
# Returns None
@type_check
def add5(x:str,y:str)-> None:
    print(_prompt + "add5 invoked: no return value")


if __name__ == '__main__':
    print(_prompt + "Invoking foo(2,' is ','2.0')...")
    print(_prompt + "foo(2,' is ','2.0')= "+ (str) (foo(2,' is ','similar to 2.0')))
    print(_prompt + "Invoking add1(2,3)...")
    print(_prompt + "add1(2,3)= "+ (str) (add1(2,3)))
    print(_prompt + "Invoking add1(1,3.5)...")
    print(_prompt + "add1(1,3.5)= "+ (str) (add1(1,3.5)))
    print(_prompt + "Invoking add2(2,3.3)...")
    print(_prompt + "add2(2,3.3)= "+ (str) (add2(2,3.3)))
    print(_prompt + "Invoking add3(2,3)...")
    print(_prompt + "add3(2,3)= "+ (str) (add3(2,3)))
    print(_prompt + "Invoking add3(2.0,3.14)...")
    print(_prompt + "add3(2.0,3.14)= "+ (str) (add3(2.0,3.14)))
    print(_prompt + "Invoking add3(3.14, 2)...")
    print(_prompt + "add3(3.14, 2)= "+ (str) (add3(3.14, 2)))
    print(_prompt + "Invoking add4(3,3)...")
    # 'add4' uses union types in annotations
    print(_prompt + "add4(3,3)= "+ (str) (add4(3,3)))
    print(_prompt + "Invoking add4(2.3,3.2)...")
    print(_prompt + "add4(2.3,3.2)= "+ (str) (add4(2.3,3.2)))
    print(_prompt + "Invoking add4('hel','lo')...")
    print(_prompt + "add4('hel','lo')= "+ (str) (add4('hel','lo')))
    print(_prompt + "Invoking add5('hel','lo')...")
    print(_prompt + "add5('hel','lo')= "+ (str) (add5('hel','lo')))
    # Nested invocation of 'add1'
    print(_prompt + "Invoking add1(3, add1(2,5))...")
    print(_prompt + "add1(3, add1(2.0,5))= "+ (str) (add1(3, add1(2.0,5))))



