import os
import fileinput
import fnmatch

def findReplace(directory, find, replace, filePattern):
    for path, dirs, files in os.walk(os.path.abspath(directory)):
        for filename in fnmatch.filter(files, filePattern):
            filepath = os.path.join(path, filename)
            with open(filepath) as f:
                s = f.read()
            s = s.replace(find, replace)
            with open(filepath, "w") as f:
                f.write(s)

packageName = input("Enter new project's package name (e.g. org.umbrellahq.baseapp):")
parts = packageName.split(".")

if len(parts) != 3:
    print("Please use a package name of 3 parts only")
    exit()
else:
    kotlinFilesList = [
            "view/src/main/java/org/umbrellahq/baseapp/activities/",
            "view/src/main/java/org/umbrellahq/baseapp/adapters/",
            "view/src/main/java/org/umbrellahq/baseapp/fragments/",
            "view/src/main/java/org/umbrellahq/baseapp/mappers/",
            "view/src/main/java/org/umbrellahq/baseapp/models/",
            "view/src/androidTest/java/org/umbrellahq/baseapp/",
            "view/src/test/java/org/umbrellahq/baseapp/",

            "database/src/main/java/org/umbrellahq/database/",
            "database/src/main/java/org/umbrellahq/database/daos/",
            "database/src/main/java/org/umbrellahq/database/models/",
            "database/src/main/java/org/umbrellahq/database/type_converters/",
            "database/src/androidTest/java/org/umbrellahq/database/",
            "database/src/test/java/org/umbrellahq/database/",

            "network/src/main/java/org/umbrellahq/network/clients/",
            "network/src/main/java/org/umbrellahq/network/daos/",
            "network/src/main/java/org/umbrellahq/network/models/",
            "network/src/test/java/org/umbrellahq/network/",

            "repository/src/main/java/org/umbrellahq/repository/mappers/",
            "repository/src/main/java/org/umbrellahq/repository/models/",
            "repository/src/main/java/org/umbrellahq/repository/repositories/",
            "repository/src/test/java/org/umbrellahq/repository/",

            "viewmodel/src/main/java/org/umbrellahq/viewmodel/mappers/",
            "viewmodel/src/main/java/org/umbrellahq/viewmodel/models/",
            "viewmodel/src/main/java/org/umbrellahq/viewmodel/viewmodels/",
            "viewmodel/src/test/java/org/umbrellahq/viewmodel/",

            "util/src/main/java/org/umbrellahq/util/",
            "util/src/main/java/org/umbrellahq/util/enums/",
            "util/src/main/java/org/umbrellahq/util/extensions/",
            "util/src/main/java/org/umbrellahq/util/interfaces/",
            "util/src/test/java/org/umbrellahq/util/"
    ]

    for kotlinFile in kotlinFilesList:
        findReplace(kotlinFile, "org.umbrellahq", parts[0] + "." + parts[1], "*.kt")
        findReplace(kotlinFile, "baseapp", parts[2], "*.kt")

    os.rename("database/src/main/java/org/umbrellahq", "database/src/main/java/org/" + parts[1])
    os.rename("database/src/main/java/org", "database/src/main/java/" + parts[0])
    os.rename("database/src/androidTest/java/org/umbrellahq", "database/src/androidTest/java/org/" + parts[1])
    os.rename("database/src/androidTest/java/org", "database/src/androidTest/java/" + parts[0])
    os.rename("database/src/test/java/org/umbrellahq", "database/src/test/java/org/" + parts[1])
    os.rename("database/src/test/java/org", "database/src/test/java/" + parts[0])
    os.rename("database/schemas/org.umbrellahq.database.AppDatabase", "database/schemas/" + parts[0] + "." + parts[1] + ".database.AppDatabase")

    os.rename("network/src/main/java/org/umbrellahq", "network/src/main/java/org/" + parts[1])
    os.rename("network/src/main/java/org", "network/src/main/java/" + parts[0])
    os.rename("network/src/test/java/org/umbrellahq", "network/src/test/java/org/" + parts[1])
    os.rename("network/src/test/java/org", "network/src/test/java/" + parts[0])

    os.rename("repository/src/main/java/org/umbrellahq", "repository/src/main/java/org/" + parts[1])
    os.rename("repository/src/main/java/org", "repository/src/main/java/" + parts[0])
    os.rename("repository/src/test/java/org/umbrellahq", "repository/src/test/java/org/" + parts[1])
    os.rename("repository/src/test/java/org", "repository/src/test/java/" + parts[0])

    os.rename("viewmodel/src/main/java/org/umbrellahq", "viewmodel/src/main/java/org/" + parts[1])
    os.rename("viewmodel/src/main/java/org", "viewmodel/src/main/java/" + parts[0])
    os.rename("viewmodel/src/test/java/org/umbrellahq", "viewmodel/src/test/java/org/" + parts[1])
    os.rename("viewmodel/src/test/java/org", "viewmodel/src/test/java/" + parts[0])

    os.rename("util/src/main/java/org/umbrellahq", "util/src/main/java/org/" + parts[1])
    os.rename("util/src/main/java/org", "util/src/main/java/" + parts[0])
    os.rename("util/src/test/java/org/umbrellahq", "util/src/test/java/org/" + parts[1])
    os.rename("util/src/test/java/org", "util/src/test/java/" + parts[0])

    os.rename("view/src/main/java/org/umbrellahq/baseapp", "view/src/main/java/org/umbrellahq/" + parts[2])
    os.rename("view/src/main/java/org/umbrellahq", "view/src/main/java/org/" + parts[1])
    os.rename("view/src/main/java/org", "view/src/main/java/" + parts[0])
    os.rename("view/src/androidTest/java/org/umbrellahq/baseapp", "view/src/androidTest/java/org/umbrellahq/" + parts[2])
    os.rename("view/src/androidTest/java/org/umbrellahq", "view/src/androidTest/java/org/" + parts[1])
    os.rename("view/src/androidTest/java/org", "view/src/androidTest/java/" + parts[0])
    os.rename("view/src/test/java/org/umbrellahq/baseapp", "view/src/test/java/org/umbrellahq/" + parts[2])
    os.rename("view/src/test/java/org/umbrellahq", "view/src/test/java/org/" + parts[1])
    os.rename("view/src/test/java/org", "view/src/test/java/" + parts[0])

    androidManifestList = [
            "view/src/main/",
            "database/src/main/",
            "network/src/main/",
            "repository/src/main/",
            "viewmodel/src/main/",
            "util/src/main/"
    ]

    for androidManifest in androidManifestList:
        findReplace(androidManifest, "org.umbrellahq", parts[0] + "." + parts[1], "*.xml")
        findReplace(androidManifest, "baseapp", parts[2], "*.xml")

    findReplace("view/", "org.umbrellahq.baseapp", packageName, "*.gradle")

    databaseName = input("What do you want to name your database (e.g. myDatabase):")

    findReplace("database/src/main/java/" + parts[0] + "/" + parts[1] + "/database/", "simplyToDo", databaseName, "*.kt")

    os.remove("start.py")

    print("Done! Now test that the app runs then COMMIT and PUSH your code to your repository!")
