CREATE LOGIN FoodOrder WITH PASSWORD = '$(FOOD_ORDER_PASSWORD)';
EXEC sp_addsrvrolemember @loginame= 'FoodOrder', @rolename = 'dbcreator';
GO
EXECUTE AS LOGIN = 'FoodOrder'; 
CREATE DATABASE FoodOrder;
ALTER DATABASE FoodOrder SET AUTO_CLOSE OFF;
GO