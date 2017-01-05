make
echo "please input the file path of training data"
read file
dir=${file/src/phrase}
nfile0=${dir/.txt/_phrase0.txt}
nfile1=${dir/.txt/_phrase1.txt}
pfile0=${dir/.txt/_phrases2.txt}
pfile1=${dir/.txt/_phrases3.txt}
vfile=${dir/.txt/_phrase_vectors.bin}
sfile=${dir/.txt/_phrase_closet.txt}
time ./word2phrase -train $file -output $nfile0 -phrases $pfile0 -threshold 200 -debug 2
time ./word2phrase -train $nfile0 -output $nfile1 -phrases $pfile1 -threshold 100 -debug 2
time ./word2vec -train $nfile1 -output $vfile -cbow 1 -size 200 -min-count 5 -window 10 -negative 25 -hs 0 -sample 1e-5 -threads 20 -binary 1 -iter 15
time ./closet $vfile $sfile
