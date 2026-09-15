module MultiSet (
    MSet,
    empty,
    add,
    occs,
    elems,
    subeq,
    union,
    mapMSet,
    toList
) where

data MSet a = MS [(a, Int)]
    deriving (Show)

-- empty returns an empty MSet
empty :: MSet a
empty = MS []

-- add mset v, returns a multiset obtained by adding the element v to mset
-- if v is already present its multiplicity has to be increased by one, otherwise it has to be inserted with multiplicity 1
add :: (Eq a) => MSet a -> a -> MSet a
add (MS xs) v = case lookup v xs of
    Just n  -> MS ((v, n + 1) : filter ((/= v) . fst) xs)   -- the filter is used to remove the old entry for v before adding the new one with incremented multiplicity
    Nothing -> MS ((v, 1) : xs)

-- occs mset v, returning the number of occurrences of v in mset (an Int)
occs :: (Eq a) => MSet a -> a -> Int
occs (MS xs) v = case lookup v xs of
    Just n -> n
    Nothing -> 0

-- elems mset, returning a list containing all the elements of mset
elems :: MSet a -> [a]
elems (MS xs) = [v | (v,_) <- xs]

-- toList mset, returning a list of pairs (v, n) where v is an element of mset and n is its multiplicity
toList :: MSet a -> [(a, Int)]
toList (MS xs) = xs

-- subeq mset1 mset2, returning True if each element of mset1 is also an
-- element of mset2 with the same multiplicity AT LEAST
subeq :: (Eq a) => MSet a -> MSet a -> Bool
subeq (MS xs1) (MS xs2) = all (\(v, n1) -> case lookup v xs2 of
                                                Just n2 -> n2 >= n1
                                                Nothing -> False) xs1

-- union mset1 mset2, returning an MSet having all the elements of mset1 and
-- of mset2, each with the sum of the corresponding multiplicities
union :: (Eq a) => MSet a -> MSet a -> MSet a
union (MS xs1) (MS xs2) = MS (combine xs1 xs2)
    where
        combine [] ys = ys
        combine ((v,n1) : xs) ys = case lookup v ys of
            Just n2 -> (v, n1+n2) : combine xs (filter ((/= v) . fst) ys)   -- the filter is used to remove the old entry for v in ys before adding the new one with summed multiplicity
            Nothing -> (v, n1) : combine xs ys

-- takes a function f and an MSet of type a as arguments, and returns the MSet of type b 
-- obtained by applying f to all the elements of its second argument
mapMSet :: (Eq b) => (a -> b) -> MSet a -> MSet b
mapMSet f (MS xs) = removedups mapped where
    mapped = [(f v, n) | (v, n) <- xs]
    -- removedups used to remove duplicates from the list of pairs, summing their multiplicities to ensure that the MSet is well-formed
    removedups [] = MS []
    removedups ((v, n):xs) = case lookup v xs of 
        Just n2 -> removedups ((v, n+n2) : filter ((/= v) . fst) xs)
        Nothing -> let (MS rest) = removedups xs in MS ((v, n) : rest)

    -- TODO: Explain (in a comment in the same file) why it is not possible to define an instance of Functor for MSet by providing mapMSet as the implementation of fmap.
    -- It is not possible to define a Functor instance for MSet using mapMSet as the implementation of fmap because 
    -- the type of mapMSet is more restrictive than the type required by Functor.
    -- In fact the Functor class requires fmap to have the type fmap :: (a -> b) -> MSet a -> MSet b 
    -- where it must work for any types a and b. But the implementation of mapMSet has the type 
    -- mapMSet :: Eq b => (a -> b) -> MSet a -> MSet b
    -- The Eq b constraint is necessary because applying a function to all the elements of an MSet can produce duplicates, 
    -- and in order to maintain the MSet invariant (that no two pairs have the same value), we need to be able to detect dupicates
    -- and so we need to compare values of type b.
    -- Not every Haskell type has an Eq instance (for example, functions cannot in general be compared for equality) 
    -- Therefore, we cannot guarantee that the invariant can be preserved for every possible target type b.




-- two multisets are equal if they contain the same elements with the same
-- multiplicity, regardless of the order           
instance (Eq a) => Eq (MSet a) where
    (mset1) == (mset2) = subeq mset1 mset2 && subeq mset2 mset1

-- folding a multiset with a binary function should apply the function 
-- to the elements of the multiset, ignoring the multiplicities
instance Foldable MSet where
    foldr _ z (MS []) = z
    foldr f z (MS ((v,n):xs)) = f v (foldr f z (MS xs))

    foldl _ z (MS []) = z
    foldl f z (MS ((v,n):xs)) = foldl f (f z v) (MS xs)