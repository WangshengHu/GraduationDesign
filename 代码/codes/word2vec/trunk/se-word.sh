make
echo "please input the file path of training data:"
read file
dir=${file/src/word}
vfile=${dir/.txt/_vectors.bin}
sfile=${dir/.txt/_closet.txt}
time ./word2vec -train $file -output $vfile -cbow 1 -size 200 -min-count 5 -window 5 -negative 25 -hs 0 -sample 1e-4 -threads 20 -binary 1 -iter 15
time ./closet $vfile $sfile

