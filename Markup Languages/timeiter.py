import timeit

print(timeit.timeit('import yamlTojson; yamlTojson.main()', number=100))

print(timeit.timeit('import yamlTojsonLIBS; yamlTojsonLIBS.main()', number=100))

print(timeit.timeit('import yamlTojsonREGEX; yamlTojsonREGEX.main()', number=100))

