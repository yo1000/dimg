# Dimg

Compare images.

## Usage

```
dimg [-b64 | -f1 <imageFile1> -f2 <imageFile2> |
      -d1 <imageContainsDir1> -d2 <imageContainsDir2>]
     [-o <outLocation> [-s]]

 -?,--help           Print this message.
 -b64,--base64       Base64 format input.
                     Use only for piped standard input.
 -d1,--dir1 <arg>    Directory containing files to compare 1.
                     Must use with d2.
 -d2,--dir2 <arg>    Directory containing files to compare 2.
                     Must use with d1.
 -f1,--file1 <arg>   File to compare 1.
                     Must use with f2.
 -f2,--file2 <arg>   File to compare 2.
                     Must use with f1.
 -o,--out <arg>      Output location.
 -s,--suppress       Suppress standard output.
```

### Run from Java using jar

Compare files.

```
$ ls /dir/path/d1
A.png   B.png   C.png

$ java -jar dimg.jar -f1 /dir/path/d1/A.png -f2 /dir/path/d1/B.png
$1: /dir/path/d1/A.png
$2: /dir/path/d1/B.png
Match ratio: 0.5
```

Compare directories.

```
$ ls /dir/path/d1
A.png   B.png   C.png

$ ls /dir/path/d2
A.png   C.png

$ java -jar dimg.jar -d1 /dir/path/d1 -d2 /dir/path/d2
$1: /dir/path/d1/A.png
$2: /dir/path/d2/A.png
Match ratio: 0.5

$1: /dir/path/d1/C.png
$2: /dir/path/d2/C.png
Match ratio: 1.0
```

### Run from Maven using sources

Compare files.

```
$ ls /dir/path/d1
A.png   B.png   C.png

$ ./mvnw clean spring-boot:run -U -Drun.arguments=-f1,/dir/path/d1/A.png,-f2,/dir/path/d1/B.png
$1: /dir/path/d1/A.png
$2: /dir/path/d1/B.png
Match ratio: 0.5
```

Compare directories.

```
$ ls /dir/path/d1
A.png   B.png   C.png

$ ls /dir/path/d2
A.png   C.png

$ ./mvnw clean spring-boot:run -U -Drun.arguments=-d1,/dir/path/d1,-d2,/dir/path/d2
$1: /dir/path/d1/A.png
$2: /dir/path/d2/A.png
Match ratio: 0.5

$1: /dir/path/d1/C.png
$2: /dir/path/d2/C.png
Match ratio: 1.0
```

## Dependencies

### Apache Commons CLI

- http://commons.apache.org/proper/commons-cli/
- https://mvnrepository.com/artifact/commons-cli/commons-cli

### Apache Commons Codec

- https://commons.apache.org/proper/commons-codec/
- https://mvnrepository.com/artifact/commons-codec/commons-codec
