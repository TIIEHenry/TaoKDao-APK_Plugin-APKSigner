/*!
- Author: AnyWin
- Date: 2019-09-07
*/

var importer = JavaImporter();
function importPackage(pkg)
{
    importer.importPackage(pkg)
}
function imports(pkg)
{
    importer.importPackage(pkg)
}
function importClass(pkg)
{
    importer.importClass(pkg)
}

importPackage(Packages.java.lang);
importPackage(Packages.java.io);