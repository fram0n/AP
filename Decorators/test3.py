from type_checking_decorators import bb_type_check
# Prompt
_prompt = "==> " 

# Meaningless but complete type hints
@bb_type_check(2)
def add(x:int,y:float)-> str:
    return x+y
# Complete type hints: integer
@bb_type_check(0)
def add1(x:int,y:int) -> int:
    return x+y
# No return type hint
@bb_type_check(2)
def add2(x:int,y:int):
    return x+y
# No parameter 1 type hint
@bb_type_check(3)
def add3(x:float,y) -> float:
    return x+y
# Type unions as hints: ok with float, not with str and int
@bb_type_check(2)
def add4(x:float|int ,y:float|str) -> float|int:
    return x+y
# Returns None
@bb_type_check(2)
def add5(x:str,y:str)-> None:
    print(_prompt + "add5 invoked: no return value")


if __name__ == '__main__':
    # 'add' as in assignment text
    print(_prompt + "Invoking add(2.3,2.3)...")
    print(_prompt + "add(2.3,2.3) = "+ (str) (add(2.3,2.3)))
    print(_prompt + "Invoking add(2.3,2.3)...")
    print(_prompt + "add(2.3,2.3) = "+ (str) (add(2.3,2.3)))
    print(_prompt + "Invoking add(2.3,2.3)...")
    print(_prompt + "add(2.3,2.3) = "+ (str) (add(2.3,2.3)))
    # 'add1' decorated with 0: it should not be blocked
    print(_prompt + "Invoking add1(2,3)...")
    print(_prompt + "add1(2,3)= "+ (str) (add1(2,3)))
    print(_prompt + "Invoking add1(2.5,3)...")
    print(_prompt + "add1(2.5,3)= "+ (str) (add1(2.5,3)))
    # 'add2' invoked with string: would raise TypeError if not blocked
    print(_prompt + "Invoking add2(2,'foo')...")
    print(_prompt + "add2(2,'foo')= "+ (str) (add2(2,'foo')))
    print(_prompt + "Invoking add2('bar', 3.4)...")
    print(_prompt + "add2('bar', 3.4)= "+ (str) (add2('bar',3.4)))
    # 'add3' can get both int and float as second parameter
    print(_prompt + "Invoking add3(2.5,3.3)...")
    print(_prompt + "add3(2.5,3.3)= "+ (str) (add3(2.5,3.3)))
    print(_prompt + "Invoking add3(2.5,3)...")
    print(_prompt + "add3(2.5,3)= "+ (str) (add3(2.5,3)))
    # 'add4' uses type unions
    print(_prompt + "Invoking add4('hel','lo')...")
    print(_prompt + "add4('hel','lo') = "+ (str) (add4('hel','lo')))
    print(_prompt + "Invoking add4(' bar ', 5.6)...")
    print(_prompt + "add4(' bar ', 5.6) = "+ (str) (add4(' bar ', 5.6)))
    print(_prompt + "Invoking add4(7.6, 4)...")
    print(_prompt + "add4(7.6, 4) = "+ (str) (add4(7.6, 4)))
    print(_prompt + "Invoking add5('hel','lo')...")
    print(_prompt + "add5('hel','lo') = "+ (str) (add5('hel','lo')))
