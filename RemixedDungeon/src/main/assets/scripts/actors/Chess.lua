---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by mike.
--- DateTime: 2/20/23 12:36 AM
---

local RPD = require "scripts/lib/commonClasses"
local util = require "scripts/lib/util"
local actor = require "scripts/lib/actor"
local sunfish = require "scripts.stuff.chess.sunfish"

local x0 = 4
local y0 = 4

local x_letters = {'a','b','c','d','e','f','g','h'}

local pieces = {}

pieces_set = {
    ['r']='Eye',
    ['R']='Eye',
    ['n']='Succubus',
    ['N']='Succubus',
    ['b']='Shaman',
    ['B']='Shaman',
    ['Q']='Warlock',
    ['q']='Warlock',
    ['K']='King',
    ['k']='King',
    ['p']='Rat',
    ['P']='Rat'
}



local chess

local function chessCellFromCell(cell)
    local level = RPD.Dungeon.level

    x = level:cellX(cell) - x0
    y = level:cellY(cell) - y0

    if x >= 0 and x < 8 and y >= 0 and y < 8 then
        local chessCell = x_letters[x+1]..tostring(y+1)
        --RPD.glog("inside of board %d -> %s", cell, chessCell)
        return chessCell
    end

    --RPD.glog("outside of board %d -> %d,%d", cell, x,y)
end

local function cellFromChess(x,y)
    local level = RPD.Dungeon.level
    return level:cell(x+x0, y+y0-1)
end


return actor.init({
    act = function()
        local level = RPD.Dungeon.level

        local boardData = util.split(chess.board,"\n")
        for i,v in ipairs(boardData) do
            if i >= 3 and i <= 10 then
                local y = i - 2

                local cell = cellFromChess(0,y)

                for ii = 1, 8 do
                    local chessCell = chessCellFromCell(cell)

                    local piece = v[ii+1]

                    if pieces_set[piece] then
                        if not pieces[chessCell] or pieces[chessCell]:getEntityKind() ~= pieces_set[piece] then
                            local mob = RPD.MobFactory:mobByName(pieces_set[piece])

                            mob:setPos(cell)
                            RPD.setAi(mob,"PASSIVE")
                            level:spawnMob(mob)
                            if piece == piece:upper() then
                                mob:getSprite():hardlight(1.2,1.2,1.2)
                            else
                                mob:getSprite():hardlight(0.8,0.8,0.8)
                            end
                            pieces[chessCell] = mob
                        end
                    end

                    --RPD.glog("%s -> %s", chessCell, piece)
                    cell = cell + 1
                end

                --RPD.glog('\n')

            end

        end
--[[
        for k, v in pairs(pieces) do
            RPD.glog("%s -> %s", k, v)
        end
]]
        return true
    end,
    actionTime = function()
        return 1
    end,
    activate = function()
        chess = sunfish.new()
        pieces = {}
    end,

    cellClicked = function(cell)
        local level = RPD.Dungeon.level
        local jcell = cell+1

        chessCellFromCell(cell)

        return false
    end
})