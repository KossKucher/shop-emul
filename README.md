Shop emulator program.

Code style used: https://google.github.io/styleguide/javaguide.html

Base file name: 'base.csv'
Base file is read from the "user.dir" location.
(Check java "user.dir" system property for more details).
If no such file provided prepacked base file will be used.
Month report is generated into "user.dir"/month_report.txt file.
Base file is backed up into "user.dir" location. Existing base file will be updated.

How to run in terminal.

Preconditions:
Maven and JDK are installed.

Steps:
1. Clone this repo to some {dir}
2. cd {dir}
3. mvn package
4. java -Dfile.encoding=UTF-8 -jar target/shop-emul-1.1-jar-with-dependencies.jar

If this steps are followed the report and refreshed base will be written to {dir} directory.

Program configuration is read from resources/config.properties file.
If no such file provided the default values will be used.
