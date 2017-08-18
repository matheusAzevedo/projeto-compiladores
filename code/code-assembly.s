100: LD SP, #4000


200: LD R1, #0
208: ST i, R1
216: LD R2, #1231
224: LD R3, i
232: MUL R4, R2, R3
240: ST d, R4
248: LD R2, #i
256: LD R3, #2
264: ADD R4, R2, R3
272: LD R5, #0
280: ST i, R5
288: LD R2, i
296: LD R3, #20
304: SUB R4, R2, R3
312: BGEQZ R4, for1
320: LD R5, #1231
328: LD R6, i
336: MUL R7, R5, R6
344: ST d, R7
352: LD R2, i
360: SUB R3, R2, #1
368: ST i, R3
376: LD R2, #0
384: ST j, R2
392: LD R2, j
400: LD R3, #100
408: SUB R4, R2, R3
416: BGEQZ R4, for1
424: LD R5, #0
432: ST k, R5
440: LD R2, i
448: LD R3, #12
456: SUB R4, R2, R3
464: BGEQZ R4, for1
472: LD R5, #testando o for 
480: LD R6, i
488: ADD R7, R5, R6
496: ST s, R7
504: LD R2, #i
512: LD R3, #2
520: MUL R4, R2, R3
528: BR *0(SP)


600: LD R5, #ola
608: ST ola, R5
616: LD R2, ola
624: BR *0(SP)


700: ADD SP, SP, #testaRetornoEChamadaDeMetodosize
708: ST 0(SP), #724
716: BR 600
724: SUB SP, SP, #testaRetornoEChamadaDeMetodosize
732: ST b, R1
740: LD R2, #10
748: ST retorno, R2
756: LD R2, retorno
764: BR *0(SP)


800: LD R3, #10
808: ST retorno, R3
816: LD R2, #10
824: LD R3, #0
832: SUB R4, R2, R3
840: BLTZ R4, 864
848: LD R5, #10
856: BR 872
864: LD R5, #0
872: ST a, R5
880: LD R2, #a
888: LD R3, #b
896: LD R4, #1
904: LD R5, retorno
912: ADD R6, R4, R5
920: ADD R7, R5, R6
928: ADD R8, R6, R7
936: ST b, R8
944: LD R2, #a
952: LD R3, b
960: LD R4, a
968: ADD R5, R3, R4
976: ADD R6, R4, R5
984: ST c, R6
992: LD R2, a
1000: BR *0(SP)


1100: LD R3, a
1108: BR *0(SP)


1200: LD R4, #1
1208: BR *0(SP)


1300: ADD SP, SP, #chamarsobrecargasize
1308: ST 0(SP), #1324
1316: BR 1200
1324: SUB SP, SP, #chamarsobrecargasize
1332: LD R2, #21
1340: ADD SP, SP, #chamarsobrecargasize
1348: ST 0(SP), #1364
1356: BR 1100
1364: SUB SP, SP, #chamarsobrecargasize
1372: BR *0(SP)
