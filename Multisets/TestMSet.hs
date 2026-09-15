import MultiSet

-- reads a text file whose name is passed as argument and returns a new MSet 
-- containing the ciao of all the words of the file, each with the corresponding mutiplicity
readMSet :: String -> IO (MSet String)
readMSet filename = do
    content <- readFile filename
    return (foldl add empty (words content))

-- given a multiset and a file name, writes in the file, one per line, each element of the multiset 
-- with its multiplicity in the format “<elem> - <multiplicity>"
writeMSet :: MSet String -> String -> IO ()
writeMSet xs filename = writeFile filename content
    where
        content = unlines [v ++ " - " ++ show n | (v, n) <- toList xs]

main :: IO ()
main = do
    m1 <- readMSet "aux_files/anagram.txt"
    m2 <- readMSet "aux_files/anagram-s1.txt"
    m3 <- readMSet "aux_files/anagram-s2.txt"
    m4 <- readMSet "aux_files/margana2.txt"
    -- i. Multisets m1 and m4 are not equal, but they have the same elements;
    -- ii. Multiset m1 is equal to the union of multisets m2 and m3;
    print (m1 == m4) -- False
    print (elems m1 == elems m4) -- on the assignment file it says that it is supposed to return True, 
                                 -- but it returns False in fact I noticed that the word "bead" it's present in m1 but not in m4
    print (m1 == union m2 m3) -- True

    -- writes multisets m1 and m4 to files anag-out.txt and gana-out.txt, respectively.
    writeMSet m1 "aux_files/anag-out.txt"
    writeMSet m4 "aux_files/gana-out.txt"



