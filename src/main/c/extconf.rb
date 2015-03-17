require 'mkmf'

$CFLAGS << " -fvisibility=hidden "

dir_config('github/markdown')
create_makefile('github/markdown')
