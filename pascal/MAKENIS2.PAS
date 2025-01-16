type
  rec=record
    txt:string[8];
    book,prk,psk:char;
  end;
var
  fdbf:file of char;
  fnis:file of rec;
  nis:rec;
  i:byte;
  count:longint;
begin
  writeln;
  assign(fdbf,'\bible7\conc.dbf');
  reset(fdbf);
  seek(fdbf,162);
  assign(fnis,'conc2.nis');
  rewrite(fnis);
  count:=0;
  repeat
    inc(count);
    nis.txt[0]:=#8;
    read(fdbf,nis.txt[0]);
    nis.txt[0]:=#8;
    for i:=1 to 8 do
      read(fdbf,nis.txt[i]);
    read(fdbf,nis.book,nis.prk,nis.psk);
    write(fnis,nis);
    write(count:8);
  until count=305421;
  close(fdbf);
  close(fnis);
end.

