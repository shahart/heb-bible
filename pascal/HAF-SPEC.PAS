{$A+,B-,D-,G+,I+,O-,P-,Q-,R-,S-,T-,V+,X+}
{$m 1024,0,0}
uses crt,dos;
const
  which:array ['1'..'2'] of string[6]=('אשכנז','ספרדים');
  p:array [1..30,1..3] of byte=((46,45,33),(40,53,32),(37,32,32),(39,32,32),(44,62,32),(43,88,45),(50,32,32),(56,45,32),
    (41,39,33),(45,69,49),(41,39,85),(56,33,45),(41,38,71),(42,43,32),(39,46,32),(45,67,47),(45,76,47),(57,34,35),(37,36,33),
    (42,54,32),(45,68,32),(40,53,32),(43,41,63),(45,32,32),(53,34,32),(44,39,44),(43,86,37),(43,97,32),(39,51,49),(43,86,37));
  w:array [12..13,1..3] of byte=((53,33,51),(46,45,33));
var
  y:char;
  y2:byte;
  sefer,perek,pasuk:string[3];
  i:integer;
  x:string[10];
begin
  If LastMode<2 Then
    TextMode(Co80);
  textattr:=lightgreen;
  CheckBreak:=False;
  textattr:=magenta;
  writeln('  באיזה נוסח הפטרה ברצונך לצפות? (1) אשכנז (2) ספרד':25,^j);
  textattr:=green;
  repeat
    y:=readkey;
  until y in ['1'..'2'];
  textattr:=yellow;
  writeln('** (':25,which[y],') רשימת ההפטרות המיוחדות **':27,^j);
  textattr:=lightblue;
  Writeln('��� �"��� ��� 21 �  �"��� ���� ����� 11 � �"��� ���� ��� ��� ���  1':77);
  Writeln('''� ��� ,��� 22 �   ����� �� ''� ��� 12 �    ������ ���� ��� ���  2':77);
  Writeln('�"��� ''� ��� ,��� 23 �   ����� �� ''� ��� 13 �              ���� ����  3':77);
  Writeln('''� ��� ,������ 24 �    ����� ���� ��� 14 �       ''� ��� ,���� ���  4':77);
  Writeln('�"��� ''� ��� ,������ 25 �     ���� ���� ��� 15 �       ''� ��� ,���� ���  5':77);
  Writeln('����� ,��� ���� 26 �      ��� ���� ��� 16 �        ����� ,���� ���  6':77);
  Writeln('���� ,��� ���� 27 �    ����� ���� ��� 17 �         ���� ,���� ���  7':77);
  Writeln('���� ��� ��� 28 �         ����� ��� 18 �          ''� ��� ,�����  8':77);
  Writeln('���� ��� ��� ��� 29 �       ''� ��� ,��� 19 �    �"��� ''� ��� ,�����  9':77);
  Write('���� ,����� ����� 30 � �"��� ''� ��� ,��� 20 �        ����� �"��� ��� 10':77);
  if y='2' then
    write(^m,' ':29);
  writeln(^j^j);
  textattr:=lightred;
  repeat
    gotoxy(1,wherey-1);
    write('������ ''�� -> ');
    clreol;
    readln(x);
    val(x,y2,i);
    if i>1 then
      val(copy(x,1,i-1),y2,i);
  until y2 in [1..79-ord(y)];
  str(p[y2,1]-31,sefer);
  str(p[y2,2]-31,perek);
  str(p[y2,3]-31,pasuk);
  if y='2' then
    if y2=14 then begin
      perek:='11';
      pasuk:='17';
    end else if y2=17 then
      pasuk:='18'
    else if (y2=25) or (y2=27) then begin
      str(w[y2 shr 1,1]-31,sefer);
      str(w[y2 shr 1,2]-31,perek);
      str(w[y2 shr 1,3]-31,pasuk);
    end;
  swapvectors;
  exec('Read.exe',sefer+' '+perek+' '+pasuk);
  swapvectors;
  textattr:=cyan;
  if dosError<>0 then
    writeln(^j'� Read.Exe ������ ����'#7);
End.