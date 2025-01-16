type
 rec2=record
  kri,ktiv:string[12];
  book,perek,pasuk:byte;
 end;
var
 f2:file of rec2;
 r2:rec2;
begin
 assign(f2,'errata.inf');
 reset(f2);
 repeat
  read(f2,r2);
  writeln;
  with r2 do begin
    writeln(kri);
    writeln(ktiv);
    writeln(book-31);
    writeln(perek-31);
    writeln(pasuk-31);
  end;
 until eof(f2);
end.