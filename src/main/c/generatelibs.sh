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
    rm -rf $3
    mkdir $3
    /Applications/CMake.app/Contents/bin/cmake -DCMAKE_TOOLCHAIN_FILE=$1 ./
    make
    cp $2 $3
    removeBuildFiles
}

buildLib CMakeListsWindowsToolchain.txt libmarkdown.dll ./win32
buildLib CMakeListsLinux64Toolchain.txt libmarkdown.so ./linux64
buildLib CMakeListsLinux32Toolchain.txt libmarkdown.so ./linux32
#buildLib CMakeListsLinuxArmToolchain.txt libmarkdown.so ./linuxArm

/Applications/CMake.app/Contents/bin/cmake ./
make
cp libmarkdown.dylib ./osx64
removeBuildFiles
