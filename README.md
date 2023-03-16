# Log Client
This tool allows for aggregated search of log access log files using regular expressions and date filtering ranges.
It accomplishes this by sending http requests to a Log Server instance. Multiple Log Server instances can be queried at once.
The results from all servers are aggregated together and a limited amount are displayed with the option to write all results to file.
It also supports output of results to file. Usage information can be display by running with no arguments or `-h`.

## Usage
```bash
$ java -jar log-client-fat-1.0-SNAPSHOT.jar -h
Usage: Log Client options_list
Options:
    --servers, -s -> Log server urls separated by commas. (always required) { String }
    --query, -q -> Query regular expression (always required) { String }
    --output, -o -> Output file name { String }
    --from, -f -> Datetime to filter from (dd/MMM/yyyy:HH:mm:ss Z) { String }
    --to, -t -> Datetime to filter to (dd/MMM/yyyy:HH:mm:ss Z) { String }
    --numResults, -n [10] -> Number of results to display { Int }
    --help, -h -> Usage info
```

Example Query
```bash
$ java -jar log-client-fat-1.0-SNAPSHOT.jar -s "http://127.0.0.1:8080" -q unicomp[0-9] -o results.log
Querying: http://127.0.0.1:8080/logs/search
Found 150 matches for "unicomp[0-9]"   in 5915ms
Displaying 10 of 150
unicomp6.unicomp.net - - [01/Jul/1995:00:00:06 -0400] "GET /shuttle/countdown/ HTTP/1.0" 200 3985
unicomp6.unicomp.net - - [01/Jul/1995:00:00:14 -0400] "GET /shuttle/countdown/count.gif HTTP/1.0" 200 40310
unicomp6.unicomp.net - - [01/Jul/1995:00:00:14 -0400] "GET /images/NASA-logosmall.gif HTTP/1.0" 200 786
unicomp6.unicomp.net - - [01/Jul/1995:00:00:14 -0400] "GET /images/KSC-logosmall.gif HTTP/1.0" 200 1204
unicomp6.unicomp.net - - [01/Jul/1995:00:01:41 -0400] "GET /htbin/cdt_main.pl HTTP/1.0" 200 3214
unicomp6.unicomp.net - - [01/Jul/1995:00:02:17 -0400] "GET /facilities/lcc.html HTTP/1.0" 200 2489
unicomp6.unicomp.net - - [01/Jul/1995:00:02:20 -0400] "GET /images/ksclogosmall.gif HTTP/1.0" 200 3635
unicomp6.unicomp.net - - [01/Jul/1995:00:02:21 -0400] "GET /images/kscmap-tiny.gif HTTP/1.0" 200 2537
unicomp6.unicomp.net - - [01/Jul/1995:00:03:09 -0400] "GET /images/lcc-small2.gif HTTP/1.0" 200 58026
unicomp6.unicomp.net - - [01/Jul/1995:00:04:16 -0400] "GET /ksc.html HTTP/1.0" 200 7074
Writing results to file  results.log
```

## Building
To build gradle project just execute the build task using the wrapper.

```bash
./gradlew build
```

This will compile and build jars. To use as a command line executable use the generated 'fat' jar. It should look something like this under `build/libs/`
```
log-client-fat-1.0-SNAPSHOT.jar
```

If you're running from IDE such as Intellij just create a run configuration using the `main` function in `Main.Kt`.
Then provide program arguments in the configuration.

Example program arguments for runtime configuration in IDE.

```aidl
-s "http://127.0.0.1:8080,http://127.0.0.1:8080" -q unicomp[0-9] -o results.log
```