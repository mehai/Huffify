# Huffify
TEXT FILE COMPRESSOR USING THE HUFFMAN CODING
=====================================
HOW TO USE
=====================================

To use the compressor, you need to use the JVM by running the
Huffify.class with the proper arguments:

-> if you need help: --help

-> if you want to compress a file: filename

-> if you want to decompress a file : -d filename

(! filename for decompression needs to be the same as the one for compression )

(! Huffify.class is part of the package huffify )

HOW IT WORKS
===================================

When you COMPRESS a file, 2 other files will be created: filename.ser
and filename.huff (filename = the initial name of the file). These 2
created files are all you need for decompression. If one of them is missing,
decompression is not possible.

*What do they contain?

---> .ser file contains a HASHSET needed to create the HuffmanTree

---> .huff file contains a BITSET representing the encoded compressed text

FOR DEVELOPERS
===================================

If you want to understand this project, you need to understand the concept of
Huffman coding, how it works and what is its purpose:
https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/

I suggest you start the documentation with the Huffify class (main, compress, decompress method).

The only relevant file in the doc directory is index.html!

Feel free to push improvements to this project if you wish!
