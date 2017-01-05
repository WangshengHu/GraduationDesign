make
echo "please input the file path of training data:"
read file
dir=${file/src/word}
vfile=${dir/.txt/_classes.txt}
sfile=${dir/.txt/_classes.sorted.txt}
time ./word2vec -train $file -output $vfile -cbow 1 -size 200 -window 8 -negative 25 -hs 0 -sample 1e-4 -threads 20 -iter 15 -classes 2000 
sort $vfile -k 2 -n > $sfile
echo The word classes were saved to file $sfile
