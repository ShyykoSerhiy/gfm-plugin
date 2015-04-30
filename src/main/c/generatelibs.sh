#!/bin/sh
removeBuildFiles(){
    rm -rf ./CMakeFiles
    rm -rf cmake_install.cmake
    rm -rf CMakeCache.txt
    rm -rf Makefile
    rm -rf libmarkdown.*
}

buildLib(){
    removeBuildFiles
    rm -rf $2
    mkdir $2
    if [ -z "$3" ]; then
        cmake ./
    else
        cmake -DCMAKE_TOOLCHAIN_FILE=$3 ./
    fi
    make
    cp $1 $2
    removeBuildFiles
}

buildLib libmarkdown.dll ./win32  CMakeListsWindowsToolchain.txt
buildLib libmarkdown.so ./linux64 CMakeListsLinux64Toolchain.txt
buildLib libmarkdown.so ./linux32 CMakeListsLinux32Toolchain.txt
buildLib libmarkdown.dylib ./osx64 CMakeListsOSXToolchain.txt
