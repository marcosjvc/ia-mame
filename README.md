Ia Mame
=======

IaMame is a thin command line wrapper for the Mame Emulator which downloads
automatically needed system roms and software from [The Internet Archive 
Mess and Mame collections](https://archive.org/details/messmame) if they are 
not found on the rompath directory before execution.
No needs to have Gigas of roms collections on your drive anymore.

*It is still under development* but actualy works for Mame roms, softwarelist roms
and some cd-rom CHD collections (pcecd, neocd, segacd). I'm facing software 
list version differences issues between the collection and the Mame executable 
on some other (psx, saturn..) which i need to address.

The goal of `ia-mame` is to be totally transparent. So simply tell it where is
your mame executable and use it like the real mame. 

It works on Linux, should work on Mac and is planned for Windows.

Prerequisites
-------------

- Java 1.7+

- Mame 0.161+

Be sure your Mame copy works as expected before trying IaMame, especially 
be sure that one of the paths on the rompath parameter is writable. IaMame 
will store downloaded files on the first writable found.

The `hash` path parameter is important too because IaMame relies on its
content.

Installation
------------

Simply download the 
[release tarball](https://github.com/TiBeN/ia-mame/releases/latest), then 
unzip it.

### Compilation from the sources

The compilation requires Maven.

- git clone this repository:

```bash
$ git clone https://github.com/TiBeN/ia-mame
```

- Build and package using maven:

```bash
$ cd /path/to/ia-mame
$ mvn package
```

Configuration
-------------

If Mame is available on your $PATH environment var, there is nothing to do, 
IaMame will find it itself if its name matches `mame[64][.exe]` 

Otherwise, you can edit the file `etc/config` and set the `mame.binary` 
parameter to the absolute path of your mame executable.

There is no need to configure anything else.

Usage
-----

Use it exactly the way you use Mame.

- Let's try Street Fighter 2 arcade board:

```bash
$ ./bin/ia-mame sf2
INFO: Download from archive.org missing rom files: [sf2] for machine "Street Fighter II: The World Warrior (World 910522)"
```

- Let's try Columns on the sega master system:

```bash
$ ./bin/ia-mame sms columns
INFO: Download from archive.org missing rom files: [sms] for machine "Master System II"
INFO: Download from archive.org missing software file: Software: [device: sms_cart, name: Columns (Euro, USA, Bra, Kor) (columns), publisher: Sega, machine: Master System II])
```
