import inspect
from typing import Any, get_args, get_origin


# @param type_hint: the type hint to check against
# @param value: the value to check
# @return: True if the value matches the type hint, False otherwise
def _matches_type(value, type_hint):
    if type_hint is inspect._empty or type_hint is Any:
        return True
    
    if type_hint is None:
        return value is None

    # check for Union types
    args = get_args(type_hint)
    if args:
        return any(_matches_type(value, arg) for arg in args)

    # check for generic types (e.g., List[int], Dict[str, int])
    origin = get_origin(type_hint)
    if origin is None:
        return isinstance(type_hint, type) and isinstance(value, type_hint)

    try:
        return isinstance(value, origin)
    except TypeError:
        return True
    
# uses introspection to print, for each argument of the decorated function, the type hint and the actual parameter of the function call, with its type
def print_types(func):
    def wrapper(*args, **kwargs):
        # get the function's signature
        sig = inspect.signature(func)
        # get the function's parameters
        params = sig.parameters
        # get the function's result type hint
        result_type_hint = sig.return_annotation
        
        # print the type hints and actual parameters
        for i, (name, param) in enumerate(params.items()):
            # get the type hint for the parameter
            type_hint = param.annotation
            # get the actual parameter value
            if i < len(args):
                actual_par = args[i]
            else:
                actual_par = kwargs.get(name, None)
            # print the type hint and actual parameter value with its type
            print(f"Formal par '{name}':{type_hint}; actual par '{actual_par}':{type(actual_par)}")
        
        # print the result type hint and the actual result with its type
        result = func(*args, **kwargs)
        print(f"Result type {result_type_hint}; actual result '{result}':{type(result)}")
        return result
    return wrapper

# refines the behaviour of print_types as follows: 
# 1) it only prints something for one parameter or for the result of the function call if there is a disagreement between the value type and a type hint, otherwise nothing is printed; 
# 2) each parameter is identified by its position in the parameter list starting from 0, not by its name
def type_check(func):
    def wrapper(*args, **kwargs):
        # get the function's signature
        sig = inspect.signature(func)
        # get the function's parameters
        params = sig.parameters
        # get the function's result type hint
        result_type_hint = sig.return_annotation
        
        # print the type hints and actual parameters
        for i, (name, param) in enumerate(params.items()):
            # get the type hint for the parameter
            type_hint = param.annotation
            # get the actual parameter value
            if i < len(args):
                actual_par = args[i]
            else:
                actual_par = kwargs.get(name, None)
            # print the type hint and actual parameter value with its type only if there is a disagreement between the value type and the type hint
            if not _matches_type(actual_par, type_hint):
                print(f"Parameter '{i}' has value '{actual_par}', not of type '{type_hint}'")
        
        # print the result type hint and the actual result with its type
        result = func(*args, **kwargs)
        if not _matches_type(result, result_type_hint):
            print(f"Result is '{result}', not of type '{result_type_hint}'")
        return result
    return wrapper

# enriches type_check in two ways: 
# 1) if there is at least one disagreement between a value type and a type hint for one parameter, the function is blocked, 
# i.e. it is not invoked at all and the decorated function returns None, without checking the result type; 
# 2) the decorator can block the function at most max_block times, where max_block is a parameter of the decorator; after that, the function is
# invoked even if there is a type mismatch for one parameter, then the type of the result is checked against the type hint, if any, and the function result is printed
def bb_type_check(max_block):
    def inner_bb_type_check(func):
        def wrapper(*args, **kwargs):
            nonlocal max_block
            # get the function's signature
            sig = inspect.signature(func)
            # get the function's parameters type hints
            params = sig.parameters
            # get the function's result type hint
            result_type_hint = sig.return_annotation
            
            # variable to count the number of mismatches between the value types and the type hints for the parameters
            mismatches_count = 0
            
            # print the type hints and actual parameters
            for i, (name, param) in enumerate(params.items()):
                # get the type hint for the parameter
                type_hint = param.annotation
                # get the actual parameter value
                if i < len(args):
                    actual_par = args[i]
                else:
                    actual_par = kwargs.get(name, None)
                # print the type hint and actual parameter value with its type only if there is a disagreement between the value type and the type hint
                if not _matches_type(actual_par, type_hint):
                    print(f"Parameter '{i}' has value '{actual_par}', not of type '{type_hint}'") 
                    mismatches_count += 1

            # if there is at least one mismatch and max_block is greater than 0, block the function invocation and decrement max_block
            if mismatches_count > 0 and max_block > 0:
                max_block -= 1
                print(f"Function blocked. Remaining blocks: {max_block}")
                return None
                      
            # print the result type hint and the actual result with its type
            result = func(*args, **kwargs)
            if not _matches_type(result, result_type_hint):
                print(f"Result is '{result}', not of type '{result_type_hint}'")
            return result
        return wrapper
    return inner_bb_type_check